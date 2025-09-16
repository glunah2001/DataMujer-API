package com.UNED.APIDataMujer.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Clase de configuración. Encargada del forzar redirecciones HTTP
 * a HTTPS.
 * */
@Configuration
public class HttpRedirectConfig {

    /**
     * Bean para la creación de servidor Tomcat embebido con un puerto adicional
     * que reenvía a HTTPS.
     * @return retorna un servidor Tomcat embebido.
     * */
    @Bean
    public TomcatServletWebServerFactory serverFactory(){
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        tomcat.addAdditionalTomcatConnectors(redirectConnector());
        return tomcat;
    }

    /**
     * Clase que define un conector tomcat HTTP como "no seguro" e indica
     * que si alguien se conecta a este sea reenviado a uno que escuche HTTPS
     * @return conector con reenvío seguro.
     * */
    private Connector redirectConnector(){
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080);
        connector.setSecure(false);
        connector.setRedirectPort(8443);
        return connector;
    }
}
