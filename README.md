# DataMujer-API // UNED - Data Mujer.
## Trabajo Final de Graduación (TFG) 2025

### Monge Kolodin Anastasia &amp; Luna Hernández Gabriel. 
- Proyecto programado. Repositorio para API RESTful para Asociación Data Mujer.

Este es un proyecto programado utilizando tecnologías Java Spring Boot. El TFG es la culminación para optar por el grado
de BACHILLERATO EN INGENIERÍA INFORMÁTICA. La Modalidad de Proyecto de Graduación es realizada en forma individual
o en grupo con un máximo de dos personas y aborda una problemática y la generación de una solución práctica para una 
situación puntual que se observa en una institución pública, privada o en una comunidad, donde se apliquen las teorías 
y las técnicas de la disciplina.

En este caso particular, los estudiantes desarrolladores se pusieron en contacto con Asociación Data Mujer para atender 
una problemática relacionada con una descentralización de información. Posterior a la exposición de la necesidad, 
situación actual, proyección a futuro y herramientas disponibles, se diseñó y construyó un sistema que cumpliera cada 
una de las exigencias dentro del límite de tiempo propuesto por la UNED.
---

## Tecnologías empleadas
- Java 21 (OpenJDK Temurin).
- Maven 3.9.9
- Spring Boot 3.5.5
- Spring Data JPA.
- Spring Security.
- Spring Mail.
- Lombok.
- Json Web Token.
- MySQL Connector.
---

## Herramientas de desarrollo empleadas
- Intellij IDEA.
- Xampp (MySQL).
- Postman (interacción con endpoints pre-cliente).
- Spring Initializer.
---

## Variables de entorno
Elemento crucial para el funcionamiento de este sistema. Por motivos de seguridad, los valores utilizados en la solución
no se nombrarán.
- APP_BASE_URL: url del servidor en el que corre la aplicación. Utilice http://localhost:8080 en caso de probar la 
aplicación.
- JWT_EXPIRATION: Tiempo de vida útil de un token de acceso medido en milisegundos (ms) desde su creación
- JWT_REFRESH_EXPIRATION: Tiempo de vida útil de un token de refresco medido en milisegundos (ms) desde su creación
- JWT_SECRET_KEY: Clave utilizada para la firma de tokens. NO REVELAR.
- SPRING_MAIL_PASSWORD: Clave de aplicación de cuenta google para la emisión de coreos. NO REVELAR.
- SPRING_MAIL_USERNAME: Cuenta utilizada para el envio de correos.
---

## Ejecución
Esta solución será puesta en producción dentro de un docker en el hosting contratado por el patrocinador. Puede probar 
su funcionalidad mediante el cliente creado para esta API.

Para pruebas locales:
1. Clone el repositorio.
2. Asegúrese de crear variables de entorno que se ajusten a lo estipulado en application.properties y sus usos en el 
sistema en su IDE o computador.
3. Levante el servicio y la base de datos MYSQL. Si quiere que Spring JPA se encargue de su creación cambie la propiedad 
'spring.jpa.hibernate.ddl-auto=validate' a 'spring.jpa.hibernate.ddl-auto=update' o 
'spring.jpa.hibernate.ddl-auto=create-drop'.
4. Una vez el servicio esté en funcionamiento, corra el proyecto en su IDE o compile y ejecute él .jar que se creará en 
la carpeta /target.
5. Utilice su herramienta de consultas HTTP preferido para probar los diferentes endpoints existentes.
---

## Endpoints
A continuación se listan todos los endpoints incluidos para ofrecer servicio al usuario. Para mayor información respecto
a parámetros o necesidades especiales de cada endpoint, revise su clase indicada.

### /auth
Acceso libre (AuthController).
- {POST} /login: Inicio de sesión.
- {POST} /refresh: refresco de token de acceso.
- {POST} /forgot-password: solicitar token de restablecimiento de contraseña.
- {POST} /reset-password: solicitar un reset de contraseña utilizando una nueva contraseña y un token de reset.

### /register
Acceso libre (PersonRegisterController).
- {POST} /physical: Registro de una persona física.
- {POST} /legal: Registro de una persona jurídica.

### /activate
Acceso libre (ActivationController).
{POST} Endpoint creado para la activación de cuentas registradas mediante un token enviado al correo electrónico de la persona.

### /user
Acceso autentificado (UserController).
- {GET} /me: obtención del perfil de la persona mediante sus credenciales de acceso.
- {PUT} /me/physical: actualización de datos de un usuario físico.
- {PUT} /me/legal: actualización de datos de un usuario jurídico.
- {PUT} /set/Role: actualización de rol de un usuario. SOLO ADMINISTRADORES.
- {PUT} /set/Affiliate: actualización de estado afiliado de un usuario. SOLO ADMINISTRADORES.
- {GET} /search/param: búsqueda de usuarios según un parámetro concreto:
  - /national-id: un usuario físico con una cédula o DIMEX concreto.
  - /legal-id: un usuario jurídico con una cédula jurídica concreta.
  - /username: un usuario con un username concreto.
  - /name: una serie de usuarios físicos que compartan el nombre indicado.
  - /surname: una serie de usuarios físicos que posean el apellido indicado.
  - /business: una serie de usuarios jurídicos que posean el nombre indicado.

### /activity
Acceso autentificado (ActivityController)
- {GET}: obtiene todas las actividades no conclusas públicadas.
- {GET} /all: obtiene todas las actividades.
- {POST}: publica una nueva actividad. SOLO ADMIN-MENTOR.
- {DELETE}: elimina una actividad por ID. SOLO ADMIN-MENTOR.

### /volunteering
Acceso autentificado restringido para los usuarios standard (VolunteeringController)
- {GET}: obtiene un voluntariado por ID. SOLO ADMIN.
- {GET} /me: obtiene todos los voluntariados de las actividades en las que se inscribió el usuario y que aún están 
pendientes a realizarse.
- {GET} /InActivity: obtiene todos los voluntariados en una actividad.
- {POST}: inserta un voluntariado en una actividad.
- {POST} /multiple: inserta múltiples voluntariados en una actividad.
- {PUT]: actualiza un voluntariado por id.
- {DELETE}: elimina un voluntariado por ID.

### /participation
Acceso autentificado (ParticipationController)
- {GET}: obtiene una participación por ID. SOLO ADMIN.
- {GET} /me: obtiene todas las participaciones de las actividades en las que se inscribió el usuario y que aún no se han
- completado o cancelado.
- {GET} /InActivity: obtiene todas las participaciones en una actividad. SOLO ADMIN O MENTOR.
- {POST}: inserta una participación en una actividad.
- {POST} /multiple: inserta múltiples participaciones en una actividad. SOLO ADMIN O MENTOR.
- {PUT} /start: actualiza el estado de una actividad 'PENDIENTE' a 'AVANZANDO'.
- {PUT} /cancel: actualiza el estado de una actividad a 'CANCELADO'.
- {DELETE}: elimina una participación por ID. SOLO ADMIN.

### /payment
Acceso autentificado (PaymentController)
- {GET}: obtiene un pago por ID. SOLO ADMIN.
- {GET} /status: obtiene una serie de pagos discriminando si está pagado o pendiente. SOLO ADMIN.
- {GET} /me: obtiene todos los pagos que ha realizado una persona. 
- {GET} /affiliates-report: enviá información de pago de usuarios afiliados. SOLO ADMIN.
- {POST}: reporta un nuevo pago por parte del usuario.
- {PUT} /paid: actualiza el estado de un pago pendiente ha pagado.
- {PUT} /unpaid: actualiza el estado de un pago reportado como realizado a pendiente. SOLO ADMIN.
- {DELETE}: elimina un pago por id.