package com.UNED.APIDataMujer.config;

import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Clase de configuración de beans necesarios para el funcionamiento de la API.
 * @author glunah2001
 * */
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final UserRepository userRepository;

    /**
     * Bean de Spring Security que carga los datos de un usuario desde la
     * BD mediante una interfaz UserDetails.
     * Usado en login y jwtFilter para cargar un usuario con credenciales válidas y
     * en SecurityFilterChain para indicar a la aplicación como autenticar usuarios.
     * @return objeto UserDetails que especifica username, password encriptado y sus roles.
     * @throws UsernameNotFoundException en caso de que el usuario no se encuentre en la BD.
     * */
    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            final User user = userRepository.findByUsername(username)
                    .orElseThrow(() ->
                            new UsernameNotFoundException("El nombre de usuario: "+username+" no se ha encontrado."));
            return org.springframework.security.core.userdetails.User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .authorities(user.getRole().name())
                    .build();
        };
    }

    /**
     * Bean que verifica credenciales mediante beans UserDetailsService y PasswordEncoder.
     * @return un proveedor de credenciales.
     * */
    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    /**
     * Bean que organiza los authentications entre los AuthenticationProvider.
     * El cliente envía un login captado por el endpoint, del endpoint va al
     * servicio que crea un UsernamePasswordAuthenticationToken, del
     * servicio se capta por el AuthenticationManager que según el tipo de
     * authentication se llama al AuthenticationProvider que obtiene los datos
     * de UserDetailsService (o arroja una excepción).
     *
     * @param config configuración general de spring security.
     * @return un gestor de autenticaciones.
     * */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Bean que encripta contraseñas con cifrado BCrypt.
     * @return Codificador.
     * */
    @Bean
    public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
}
