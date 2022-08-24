package br.edu.ifsp.spo.eventos.eventplatformbackend.common.security;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.List;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class WebSecurityConfigure {
    private final JwtService jwtService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        var accountOpenPaths = List.of(
            "/api/v1/accounts/registration",
            "/api/v1/accounts/registration/verification/{token}",
            "/api/v1/accounts/login",
            "/api/v1/accounts/refresh-token-rotation",
            "/api/v1/accounts/password/forgot",
            "/api/v1/accounts/password/reset"
        );

        var siteOpenPaths = List.of(
            "/api/v1/locations",
            "/api/v1/locations/{locationId}",
            "/api/v1/locations/{locationId}/areas",
            "/api/v1/locations/{locationId}/areas/{areaId}",
            "/api/v1/locations/{locationId}/areas/{areaId}/spaces",
            "/api/v1/locations/{locationId}/areas/{areaId}/spaces/{spaceId}",
            "/api/v1/events",
            "/api/v1/events/{eventId}",
            "/api/v1/events/{eventId}/sub-events",
            "/api/v1/events/{eventId}/sub-events/{subeventId}",
            "/api/v1/events/{eventId}/activities",
            "/api/v1/events/{eventId}/activities/{activityId}",
            "/api/v1/events/{eventId}/sub-events/{subeventId}/activities",
            "/api/v1/events/{eventId}/sub-events/{subeventId}/activities/{activityId}"
        );

        http
            .cors().and().csrf()
                .disable()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeHttpRequests((authz) -> authz
                .antMatchers(accountOpenPaths.toArray(String[]::new)).permitAll()
                .antMatchers(HttpMethod.GET, siteOpenPaths.toArray(String[]::new)).permitAll()
                .antMatchers("/api/v1/sessions/**").hasAnyRole("ADMIN", "ATTENDANT")
                .antMatchers("/api/v1/accounts/registrations/**").hasAnyRole("ADMIN", "ATTENDANT")
                .antMatchers("/api/v1/accounts/my-data/**").hasAnyRole("ADMIN", "ATTENDANT")
                .antMatchers("/api/v1/accounts/logout").hasAnyRole("ADMIN", "ATTENDANT")
                .antMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
            .addFilterBefore(new JwtAuthenticationTokenFilter(jwtService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
