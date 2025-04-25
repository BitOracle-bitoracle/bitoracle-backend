package com.BitOracle.BitOracle.config;

import com.BitOracle.BitOracle.jwt.CustomLogoutFilter;
import com.BitOracle.BitOracle.jwt.JWTFilter;
import com.BitOracle.BitOracle.jwt.JWTUtil;
import com.BitOracle.BitOracle.oauth2.CustomSuccessHandler;
import com.BitOracle.BitOracle.repository.RefreshRepository;
import com.BitOracle.BitOracle.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public SecurityConfig(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler, JWTUtil jwtUtil, RefreshRepository refreshRepository){
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        configuration.setAllowedOrigins(Collections.singletonList("https://bitoracle.netlify.app")); // 프론트 서버 주소
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Set-Cookie"));
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                }));

        //csrf disable => jwt로 stateless로 관리할 것이기 때문에 꺼도 됨
        http
                .csrf((auth) -> auth.disable());

        //Form 로그인 방식 disable => jwt, OAuth 아용할 것이기 때문에 꺼도 됨
        http
                .formLogin((auth) -> auth.disable());

        //HTTP Basic 인증 방식 disable => jwt, OAuth 아용할 것이기 때문에 꺼도 됨
        http
                .httpBasic((auth) -> auth.disable());

        //JWTFilter 추가
        http
                .addFilterBefore(new JWTFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        //oauth2
        http
                //.oauth2Login(Customizer.withDefaults());
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                );

        //경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/", "/reissue", "/api/auth/init",   // 기본 허용
                                "/v3/api-docs/**",                       // Swagger 문서 JSON
                                "/swagger-ui/**", "/swagger-ui.html",    // Swagger UI
                                "/swagger-resources/**", "/webjars/**",   // Swagger 리소스
                                "/api/predict/midnight", "/api/test/**", "api/news/main",         // 로그인 상관 없이 허용되는 api
                                "/api/community/**",
                                "/ws-upbit/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        //세션 설정 : STATELESS => jwt니까 stateless
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }
}
