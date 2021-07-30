package com.spaceymonk.mentorhub.config;


import com.spaceymonk.mentorhub.controller.LoginPageInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfigurer extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler("/error");
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler("/dashboard");

        http
                .authorizeRequests(a -> a
                        .antMatchers("/", "/error", "/assets/**").permitAll()
                        .anyRequest().authenticated()
                )
//                .exceptionHandling(e -> e
//                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
////                                .accessDeniedPage("/error")
//                )
                .csrf(c -> c
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )
                .logout(l -> l
                        .logoutSuccessUrl("/")
                )
                .oauth2Login(o -> o
                        .loginPage("/")
                        .successHandler(successHandler)
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                            failureHandler.onAuthenticationFailure(request, response, exception);
                        })
                )
                .formLogin(f -> f
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                            System.out.println("Hey you fucked up");
                            failureHandler.onAuthenticationFailure(request, response, exception);
                        })
                        .successHandler(successHandler)
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/"));
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")
                .and()
                .passwordCompare()
                .passwordEncoder(new BCryptPasswordEncoder())
                .passwordAttribute("userPassword");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginPageInterceptor());
    }
}
