package com.example.application;

import java.util.Collections;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import org.atmosphere.cpr.AtmosphereServlet;
import org.atmosphere.cpr.ContainerInitializer;
import org.atmosphere.cpr.MetaBroadcaster;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "mytodo")
@PWA(name = "My Todo", shortName = "My Todo", offlineResources = {})
@NpmPackage(value = "line-awesome", version = "1.3.0")
public class Application extends SpringBootServletInitializer implements AppShellConfigurator {
    private static class EmbeddedAtmosphereInitializer extends ContainerInitializer implements ServletContextInitializer {
        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            onStartup(Collections.emptySet(), servletContext);
        }
    }

    @Bean
    public EmbeddedAtmosphereInitializer atmosphereInitializer() {
        return new EmbeddedAtmosphereInitializer();
    }

    @Bean
    public AtmosphereServlet atmosphereServlet(){
        return new AtmosphereServlet();
    }

    @Bean
    public MetaBroadcaster metaBroadcaster() {
        return atmosphereServlet().framework().metaBroadcaster();
    }

    @Bean
    public ServletRegistrationBean<AtmosphereServlet> atmosphereServletBean() {
        // Dispatcher servlet is mapped to '/home' to allow the AtmosphereServlet
        // to be mapped to '/chat'
        final ServletRegistrationBean<AtmosphereServlet> registration = new ServletRegistrationBean<>(atmosphereServlet(), "/chat/*");
        registration.addInitParameter("org.atmosphere.cpr.packages", "sample");
        registration.addInitParameter("org.atmosphere.interceptor.HeartbeatInterceptor"
                + ".clientHeartbeatFrequencyInSeconds", "10");
        registration.setLoadOnStartup(0);
        // Need to occur before the EmbeddedAtmosphereInitializer
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    /*
    @Configuration
    static class MvcConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/").setViewName("forward:/home/home.html");
        }

    }
     */

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
