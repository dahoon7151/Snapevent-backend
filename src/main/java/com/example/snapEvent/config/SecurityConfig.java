package com.example.snapEvent.config;

import com.example.snapEvent.member.security.CustomAuthenticationEntryPoint;
import com.example.snapEvent.member.security.OAuth2.OAuth2FailureHandler;
import com.example.snapEvent.member.security.OAuth2.OAuth2SuccessHandler;
import com.example.snapEvent.member.security.jwt.ExceptionHandlerFilter;
import com.example.snapEvent.member.security.jwt.JwtAuthenticationFilter;
import com.example.snapEvent.member.security.jwt.JwtTokenProvider;
import com.example.snapEvent.member.repository.RefreshTokenRepository;
import com.example.snapEvent.member.security.OAuth2.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final OAuth2FailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .formLogin(AbstractHttpConfigurer::disable)
                // REST API이므로 basic auth 및 csrf 보안을 사용하지 않음
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer
                        .configurationSource(request -> {
                            CorsConfiguration config = new CorsConfiguration();
                            config.setAllowedOrigins(List.of("https://snapevent.site", "http://localhost:8080", "http://localhost:5173"));
                            config.setAllowedMethods(List.of("GET","POST","PATCH","PUT","DELETE","OPTION"));
                            config.setAllowCredentials(true);
                            config.setAllowedHeaders(List.of("*"));
                            config.setMaxAge(1728000L);
                            return config;
                        }))
                // JWT를 사용하기 때문에 세션을 사용하지 않음
                .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // 해당 API에 대해서는 모든 요청을 허가
                        .requestMatchers(
                                "/oauth2/authorization/google",
                                "/oauth2/authorization/naver",
                                "/oauth2/authorization/kakao",
                                "/api/members/join",
                                "/api/members/login",
                                "/api/members/reissue",
                                "/api/members/checkname",
                                "/api/crawl/**",
                                "/login",
                                "/main",
                                "/landing",
                                "/Onboarding",
                                "/api/posts/list/**").permitAll()
                        // USER 권한이 있어야 요청할 수 있음
                        .requestMatchers(
                                "/api/members/test").hasRole("USER")
                        // 이 밖에 모든 요청에 대해서 인증을 필요로 한다는 설정
                        .anyRequest().authenticated())
//                        .anyRequest().permitAll()) // 테스트용 허용
                .oauth2Login(oauth2 -> oauth2
//                        .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
//                                .baseUri("/oauth2/authorization"))
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService)))
                // JWT 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행
                .addFilterAfter(new UsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, JwtAuthenticationFilter.class)
                .exceptionHandling(authenticationManager -> authenticationManager
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .build();
    }
}