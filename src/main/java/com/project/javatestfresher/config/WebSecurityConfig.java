package com.project.salebe.config;

import com.project.salebe.filter.JwtTokenFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;

import static org.springframework.http.HttpMethod.GET;

@Configuration
//@EnableMethodSecurity
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebMvc
@RequiredArgsConstructor
public class WebSecurityConfig {
    @Autowired
    private JwtTokenFilter jwtTokenFilter;
    //    private final CustomOAuth2UserService oauth2UserService;
    @Value("${api.prefix}")
    private String apiPrefix;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    String.format("%s/users/register", apiPrefix),
//                                    String.format("%s/admin/search", apiPrefix),
                                    String.format("%s/auth/login", apiPrefix),
                                    //healthcheck
                                    String.format("%s/healthcheck/**", apiPrefix),
                                    //swagger
                                    //"/v3/api-docs",
                                    //"/v3/api-docs/**",
                                    "/api-docs",
                                    "/api-docs/**",
                                    "/swagger-resources",
                                    "/swagger-resources/**",
                                    "/configuration/ui",
                                    "/configuration/security",
                                    "/swagger-ui/**",
                                    "/swagger-ui.html",
                                    "/webjars/swagger-ui/**",
                                    "/swagger-ui/index.html"

                            )
                            .permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/roles**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/policies/**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/categories/**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/products/**", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/products/images/*", apiPrefix)).permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/orders/**", apiPrefix)).permitAll()
                            .requestMatchers(GET,
                                    String.format("%s/users/profile-images/**", apiPrefix))
                            .permitAll()

                            .requestMatchers(GET,
                                    String.format("%s/order_details/**", apiPrefix)).permitAll()

                            .anyRequest()
                            .authenticated();
                    //.anyRequest().permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable);
//        http.securityMatcher(String.valueOf(EndpointRequest.toAnyEndpoint()));
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://*"); // Thay đổi thành URL của ứng dụng Flutter web hoặc thêm *
        config.addAllowedOriginPattern("*"); // Cho phép tất cả các nguồn gốc
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;

    }
}