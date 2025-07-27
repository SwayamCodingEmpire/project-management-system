package com.cozentus.pms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.cozentus.pms.filters.JwtAuthenticationFilter;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private static final String[] RESOURCE_URLS = {
    		"/resource-dashboard/**",
    		"/timesheet",
    		"/timesheet/submit",
    };
    
    private static final String[] PUBLIC_URLS = {
    		"/public/**",  // public endpoints
    		"/poc/**",
    		"/actuator/**",
    		"/swagger-ui/index.html",
    	       "/server1/**",
               "/v3/api-docs",
               "/v2/api-docs",
               "/v3/api-docs/**",
               "/swagger-resources/**",
               "/swagger-ui/**",
               "/webjars/**",
               "/swagger-ui.html",
    		

    };
    
    
    
    private static final String[] DM_URLS = {
    		"/clients/**",  // client management
    		"/manager-dashboard/project-count",
    		"/manager-dashboard/projects-by-pm/**",
    		
    		"/project",
    		"/project/default-mail-config",
    		"/project/types",
    		"/resource-allocation/allocate-to-dm/**",
    		"/resource-allocation/deAllocate-from-dm/**"
    		
    	};
    

    
    
    private static final String[] PM_URLS = {
    		"/pm-dashboard/**"
    	};
    
    private static final String[] MANAGER_URLS = {
    		"/manager/**",  // manager operations
    		"/manager-dashboard/**",  // manager dashboard
    		"/project/*/mail-config",
    		"/resource-allocation/**",
    		"/timesheet/**",
    		"/users/**"
    		
		};

    




    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authenticationProvider = authenticationProvider;
    }
    



    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.authorizeHttpRequests(authorize->{
            authorize.requestMatchers(PUBLIC_URLS).permitAll();
            authorize.requestMatchers(RESOURCE_URLS).hasRole("RESOURCE");
            authorize.requestMatchers(DM_URLS).hasRole("DELIVERY_MANAGER");
            authorize.requestMatchers(PM_URLS).hasRole("PROJECT_MANAGER");
            authorize.requestMatchers(MANAGER_URLS).hasAnyRole("DELIVERY_MANAGER", "PROJECT_MANAGER");
            authorize.anyRequest().authenticated();
        }).sessionManagement(session->{
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        })
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable());
        return httpSecurity.build();
    }
    
    @Bean
    public OpenAPI openAPI(){
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info().title("Resource Management System APIs")
                        .description("This is the backend of the Resource Management System, which provides APIs for managing resources, projects, and timesheets.")
                        .version("v2.0")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
    
}
