package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.BaseVolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringUpdateDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringWrapperDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Volunteering;
import com.UNED.APIDataMujer.enums.Role;
import com.UNED.APIDataMujer.exception.BusinessValidationException;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.PaginationUtil;
import com.UNED.APIDataMujer.mapper.VolunteeringMapper;
import com.UNED.APIDataMujer.repository.ActivityRepository;
import com.UNED.APIDataMujer.repository.VolunteeringRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Clase encargada de gestionar la lógica de negocio relacionada con los
 * voluntariados de las personas en actividades. Cuando se refiere a
 * Voluntariados se refieren a puestos como: Organizador, Mentor encargado,
 * Moderador, etc.
 * @author glunah2001
 * @see VolunteeringService
 * */
@Service
@RequiredArgsConstructor
public class VolunteeringServiceImpl implements VolunteeringService{

    private final UserService userService;
    private final VolunteeringMapper volunteeringMapper;

    private final VolunteeringRepository volunteeringRepository;
    private final ActivityRepository activityRepository;

    /**
     * Función de interfaz. Función encargada de obtener del repositorio un voluntariado a través de su id único
     * @param id identificador del voluntariado.
     * @return Dto. Del voluntariado.
     * @throws ResourceNotFoundException en caso de que el voluntariado no exista.
     * */
    @Override
    public VolunteeringDTO getVolunteering(long id) {
        var volunteering = volunteeringRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se ha encontrado un voluntariado registrado " +
                                "con el id: "+id+".")
                );

        return volunteeringMapper.toDto(volunteering);
    }

    /**
     * Función de interfaz. Esta función se encarga de obtener todos los voluntariados
     * pendientes (de actividades sin finalizar) de la persona que solicita.
     * @param auth credenciales de autentificación del usuario.
     * @return lista de voluntariados con detalles de la actividad.
     * */
    @Override
    public SimplePage<VolunteeringDTO> getMyPendingVolunteering(Authentication auth, int page) {
        final var user = userService.getMyUser(auth);
        Pageable pageable = PageRequest.of(page, 25, Sort.by("startShift").ascending());
        Page<Volunteering> volunteering =
                volunteeringRepository.findByUserIdAndActivityIsFinalizedFalse(user.getId(), pageable);

        return PaginationUtil.wrapInPage(volunteering, volunteeringMapper::toDto);
    }

    /**
     * Función de interfaz. Esta función se encarga de obtener todos los voluntariados
     * de una actividad.
     * @param activityId id de la actividad a buscar.
     * @return lista de voluntariados con detalles de la actividad.
     * */
    @Override
    public SimplePage<VolunteeringDTO> getVolunteeringForAnActivity(final Authentication auth,
                                                                    long activityId,
                                                                    int page) {
        var user = userService.getMyUser(auth);

        if(!isUserOrganizer(activityId, user.getId()) && user.getRole() != Role.ROLE_ADMIN)
            throw new BusinessValidationException("La actividad "+activityId+" y sus voluntariados no " +
                    "pueden ser consultadas por su persona");

        Pageable pageable = PageRequest.of(page, 25, Sort.by("id").ascending());
        Page<Volunteering> volunteering =
                volunteeringRepository.findByActivityId(activityId, pageable);
        return PaginationUtil.wrapInPage(volunteering, volunteeringMapper::toDto);
    }

    /**
     * Función de interfaz. Función encargada de insertar un voluntariado de organizador.
     * Se ejecuta cuando una actividad es registrada. Rollback en caso de fallar.
     * @param activity actividad del voluntariado.
     * @param username usuario del voluntariado.
     * @param startDate fecha de inicio del voluntariado.
     * @param endDate fecha de fin del voluntariado.
     * @throws BusinessValidationException en caso de que exista un conflicto de horarios del
     * organizador.
     * */
    @Override
    public void createOrganizerVolunteering(String username,
                                            Activity activity,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate) {

        final var user = userService.getUserByUsername(username);

        if(!user.isActive())
            throw new BusinessValidationException("Por integridad y consistencia de los datos, la actividad no " +
                    "puede ser organizada por una persona cuya cuenta está inactiva actualmente.");

        var conflict = volunteeringRepository.existsOrganizerConflict(
                user.getId(), startDate, endDate);
        if(conflict)
            throw new BusinessValidationException("El usuario "+username+" ya organiza una actividad " +
                    "cuyo horario entra en conflicto con esta nueva actividad.");

        var volunteering = volunteeringMapper.toEntity(user, activity);
        volunteeringRepository.save(volunteering);
    }

    /**
     * Función de interfaz. Función encargada de analizar el lote de voluntariados
     * antes de insertar los voluntariados.
     * @return id de la actividad para consultar sus voluntariados.
     * @throws BusinessValidationException en caso de que exista una inconsistencia en los voluntariados.
     * */
    @Override
    @Transactional
    public long createVolunteering(final Authentication auth, VolunteeringWrapperDTO dto) {
        var user = userService.getMyUser(auth);
        var activityId = dto.activityId();
        if(user.getRole() != Role.ROLE_ADMIN && isUserOrganizer(activityId, user.getId()))
            throw new BusinessValidationException("Esta operación no puede ser realizada por su persona " +
                    "por falta de rol o estado ORGANIZADOR PRINCIPAL.");

        var list = dto.volunteering();
        if(list.isEmpty())
            throw new BusinessValidationException("El lote voluntariados está vacío.");


        if(!sameActivityVolunteering(activityId, list))
            throw new BusinessValidationException("Existe una inconsistencia en el lote voluntariados: " +
                    "No todos los voluntariados están dirigidos a la misma actividad");

        list.forEach(this::createVolunteering);
        return activityId;
    }

    /**
     * Función de interfaz. Función encargada de preparar la inserción de un voluntariado
     * propio.
     * @param dto Dto. Con información del voluntariado.
     * @param auth credenciales del usuario.
     * @return Dto. Del voluntariado insertado.
     * */
    @Override
    @Transactional
    public VolunteeringDTO createMyVolunteering(final Authentication auth,
                                                BaseVolunteeringRegisterDTO dto) {
        var user = userService.getMyUser(auth);
        var volunteering = new VolunteeringRegisterDTO(dto, user.getUsername());
        return createVolunteering(volunteering);
    }

    /**
     * Función de interfaz. Función encargada de insertar en la base de datos el voluntariado
     * de un usuario.
     * @param dto Dto. Con información del voluntariado.
     * @return Dto. Del voluntariado insertado.
     * @throws BusinessValidationException en caso de que una regla de negocio sea violada.
     * @throws ResourceNotFoundException en caso de que no se encuentre el voluntariado del organizador.
     * */
    @Transactional
    private VolunteeringDTO createVolunteering(VolunteeringRegisterDTO dto) {
        var volunteeringData = dto.volunteeringData();

        final var activity = getActivity(volunteeringData.activityId());
        var username = dto.username();
        final var user = userService.getUserByUsername(username);


        validateVolunteering(activity,
                dto.volunteeringData().startShift(),
                dto.volunteeringData().endShift(),
                user.getId(),
                user.getUsername());

        var volunteering = volunteeringMapper.toEntity(user, activity, dto);
        var myVolunteering = volunteeringRepository.save(volunteering);
        return volunteeringMapper.toDto(myVolunteering);
    }

    /**
     * Función de interfaz encargada de verificar si el usuario relacionado con una actividad
     * es el organizador de la misma.
     * @param activityId identificador de la actividad.
     * @param userId identificador del usuario.
     * @return booleano de confirmación o negación.
     * */
    @Override
    public boolean isUserOrganizer(long activityId, long userId) {
        var organizerId = volunteeringRepository
                .findOrganizerIdByActivityId(activityId).orElseThrow(() ->
                        new ResourceNotFoundException("Ocurrió un error al intentar recuperar al organizador " +
                                "de la actividad "+activityId+".")
                );
        return organizerId.equals(userId);
    }

    /**
     * Función de interfaz. Se encarga de actualizar a los voluntariados propios de una
     * persona.
     * @param volunteeringId identificador del voluntariado a actualizar.
     * @param dto nueva información del voluntariado.
     * @throws ResourceNotFoundException en caso de que el voluntariado original no pueda ser recuperado de la bd.
     * @throws BusinessValidationException en caso de que el voluntariado no pueda ser actualizado por ser el
     * voluntariado del organizador.
     * */
    @Override
    @Transactional
    public VolunteeringDTO updateVolunteering(long volunteeringId, final VolunteeringUpdateDTO dto) {
        var volunteering = volunteeringRepository.findById(volunteeringId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se ha encontrado un voluntariado registrado " +
                                "con el id: "+volunteeringId+".")
                );
        var activity = volunteering.getActivity();

        if(activity.isFinalized())
            throw new BusinessValidationException("La actividad asociada a este voluntariado a concluido");

        if(volunteering.isMainOrganizer())
            throw new BusinessValidationException("Dado que este voluntariado está marcado como " +
                    "\"voluntariado de organizador principal\" no es posible actualizarlo.");

        final var user = volunteering.getUser();

        validateVolunteering(activity,
                dto.startShift(),
                dto.endShift(),
                user.getId(),
                user.getUsername());

        volunteering.setStartShift(dto.startShift());
        volunteering.setEndShift(dto.endShift());
        volunteering.setActivityRole(dto.activityRole());

        var updatedVolunteering = volunteeringRepository.save(volunteering);
        return volunteeringMapper.toDto(updatedVolunteering);
    }

    /**
     * Función de interfaz. Se encarga de dar de baja a un voluntariado.
     * @param volunteeringId identificador del voluntariado a dar de baja.
     * @param auth credenciales del usuario que desea eliminar el voluntariado.
     * @throws ResourceNotFoundException en caso de que el voluntariado no exista en la base de datos.
     * @throws BusinessValidationException en caso de que el voluntariado no pueda ser eliminado por
     * ya sea porque es el voluntariado del organizador principal; o en caso de no ser administrador
     * la actividad está cerrada o el usuario no sea el organizador principal.
     * */
    @Override
    @Transactional
    public void deleteVolunteering(long volunteeringId, final Authentication auth) {
        var volunteering = volunteeringRepository.findById(volunteeringId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se ha podido encontrar el voluntariado a eliminar.")
                );

        if(volunteering.isMainOrganizer())
            throw new BusinessValidationException("Dado que este voluntariado está marcado como " +
                    "\"voluntariado de organizador principal\" no es posible eliminarlo.");

        final var currentUser = userService.getMyUser(auth);

        if(volunteering.getActivity().isFinalized() &&
                currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new BusinessValidationException("No es posible eliminar voluntariados " +
                    "de actividades finalizadas.");
        }

        if(currentUser.getRole() != Role.ROLE_ADMIN &&
        !currentUser.equals(volunteering.getUser()))
            throw new BusinessValidationException("Usted está intentando eliminar un voluntariado " +
                    "que no le pertenece.");

        volunteeringRepository.delete(volunteering);
    }

    /**
     * Función auxiliar encargada de verificar que en un lote de voluntariados todos
     * pertenezcan a una misma actividad.
     * @param activityId actividad a la que deberían pertenecer los voluntariados.
     * @param list lista de voluntariados.
     * @return booleano válido/inválido.
     * */
    private boolean sameActivityVolunteering(long activityId, List<VolunteeringRegisterDTO> list){
        return list.stream()
                .allMatch(dto -> dto.volunteeringData().activityId() == activityId);
    }

    /**
     * Función auxiliar encargada de solicitar al repositorio una actividad por su id.
     * @param id id único de la actividad.
     * @return entidad Activity recuperada de la bd.
     * @throws ResourceNotFoundException en caso que la actividad no exista o no pueda recuperarse.
     * */
    private Activity getActivity(long id){
        return activityRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("La actividad con id "+id+" no se ha encontrado."));
    }

    /**
     * Función auxiliar encargada de validar los datos de un voluntariado a persistir.
     * @param activity actividad del voluntariado.
     * @param startShift fecha y hora inicial del turno del voluntariado.
     * @param endShift fecha y hora final del turno del voluntariado.
     * @param userId identificador del usuario que realizará el voluntariado.
     * @param username username del usuario que realizará el voluntariado.
     * @throws BusinessValidationException en caso de que alguna regla de negocio sea quebrantada:
     * actividad finalizada, fecha de inicio posterior a la de fin, horas de voluntariado inválidas o
     * se inscriba en dos actividades de un mismo organizador que se solapen.
     * @throws ResourceNotFoundException en caso de que no se pueda recuperar el organizador de la actividad.
     * */
    private void validateVolunteering(Activity activity, LocalDateTime startShift,
                                      LocalDateTime endShift, long userId, String username){
        if(activity.isFinalized())
            throw new BusinessValidationException("La operación de voluntariado del usuario "
                    + username + " no se puede llevar a cabo porque la actividad está concluida.");

        if(!startShift.isBefore(endShift))
            throw new BusinessValidationException("La operación de voluntariado del usuario "
                    + username + " no se puede llevar a cabo porque la fecha de inicio está " +
                    "después de la fecha de cierre de su turno.");

        if(startShift.isBefore(activity.getStartDate()) ||
                endShift.isAfter(activity.getEndDate()))
            throw new BusinessValidationException("La operación de voluntariado del usuario "
                    + username + " no se puede llevar a cabo porque su fecha de voluntariado " +
                    "está fuera del rango de la actividad.");

        var shiftLength = ChronoUnit.HOURS.between(startShift, endShift);
        if(shiftLength < 1 || shiftLength > 12)
            throw new BusinessValidationException("La operación de voluntariado del usuario "
                    + username + " no se puede llevar a cabo porque los turnos deben abarcar " +
                    "de 1 hora mínimo a 12 horas máximo.");

        var organizerId = volunteeringRepository.findOrganizerIdByActivityId(activity.getId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("No ha sido posible consultar al " +
                                "organizador de la actividad."));

        var sameOrganizerConflict = volunteeringRepository.existsConflictWithSameOrganizer(
                userId, organizerId, activity.getId(), startShift, endShift);

        if(sameOrganizerConflict && !isUserOrganizer(activity.getId(), userId))
            throw new BusinessValidationException("La operación de voluntariado del usuario "
                    + username + " no se puede llevar a cabo porque no puede participar en " +
                    "dos actividades del mismo organizador que se lleguen a solapar.");
    }
}
