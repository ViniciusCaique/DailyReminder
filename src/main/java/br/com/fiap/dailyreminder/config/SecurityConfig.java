package br.com.fiap.dailyreminder.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.core.env.Environment;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
public class SecurityConfig {

    @Autowired
    AuthorizationFilter authorizationFilter;

    @Autowired
    Environment env;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .authorizeHttpRequests(auth -> {
            auth.requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/scalar/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/sign-up").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/sign-in").permitAll();

                auth.anyRequest().authenticated();
          })
          .csrf(AbstractHttpConfigurer::disable)
          .addFilterBefore(authorizationFilter, BasicAuthenticationFilter.class);


      //          .formLogin().disable()
//          .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

//        if (env.getActiveProfiles().length > 0 && env.getActiveProfiles()[0].equals("open")){
//            http.authorizeHttpRequests().anyRequest().permitAll();
//        } else {
//            http.authorizeHttpRequests().anyRequest().authenticated();
//        }

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
}
