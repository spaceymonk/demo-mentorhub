package com.spaceymonk.mentorhub.config;

import com.spaceymonk.mentorhub.config.util.CustomOidcUserService;
import com.spaceymonk.mentorhub.config.util.LoginPageInterceptor;
import com.spaceymonk.mentorhub.config.util.MyUserDetailsContextMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Configurer class of application security context.
 * It handles login operations done by LDAP and Google Authentication.
 * LDAP connection configurations done in this class.
 * Sets interceptor for not-logged in users, so that redirects them to login
 * page. It also configures accessible pages and logout operations.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 * @see WebSecurityConfigurerAdapter
 * @see WebMvcConfigurer
 * @see LoginPageInterceptor
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
@AllArgsConstructor
public class SecurityConfigurer extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final CustomOidcUserService customOidcUserService;
    private final LoginPageInterceptor loginPageInterceptor;
    private final MyUserDetailsContextMapper myUserDetailsContextMapper;

    /**
     * Password encoder for LDAP logins.
     * Current LDAP server uses BCrypt algorithm to store passwords.
     *
     * @return BCryptPasswordEncoder password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Setup interceptor for not-logged in users, so redirect them to login page.
     *
     * @param registry Autowired by the Spring framework
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginPageInterceptor);
    }

    /**
     * Configures LDAP connection.
     *
     * @param auth Autowired by Spring framework.
     */
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .ldapAuthentication()
                .userDetailsContextMapper(myUserDetailsContextMapper)
                .userDnPatterns("uid={0},ou=people")
                .groupSearchBase("ou=groups")
                .contextSource()
                .url("ldap://localhost:8389/dc=springframework,dc=org")
                .and()
                .passwordCompare()
                .passwordEncoder(passwordEncoder())
                .passwordAttribute("userPassword");
    }

    /**
     * Sets authorization details for this application.
     * Configures both LDAP and Google OAuth login entry points, accessible pages
     * and logout operations
     *
     * @param http Autowired by the Spring framework
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        SimpleUrlAuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler("/");
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler("/dashboard");

        http
                .authorizeRequests(a -> a
                        .antMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling().accessDeniedPage("/error/403").and()
                .csrf().disable()
                .logout(l -> l
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessUrl("/")
                )
                .oauth2Login(o -> o
                        .loginPage("/")
                        .userInfoEndpoint(i -> i
                                .oidcUserService(customOidcUserService))
                        .successHandler(successHandler)
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                            failureHandler.onAuthenticationFailure(request, response, exception);
                        })
                )
                .formLogin(f -> f
                        .failureHandler((request, response, exception) -> {
                            request.getSession().setAttribute("error.message", exception.getMessage());
                            failureHandler.onAuthenticationFailure(request, response, exception);
                        })
                        .successHandler(successHandler)
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .loginPage("/"));
    }
}
