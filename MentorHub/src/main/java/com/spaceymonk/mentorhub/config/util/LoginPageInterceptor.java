package com.spaceymonk.mentorhub.config.util;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Handles redirection to login page.
 * This class has used for redirecting not-logged in users to login page.
 *
 * @author spaceymonk
 * @version 1.0, 08/18/21
 */
@Service
public class LoginPageInterceptor implements HandlerInterceptor {

    UrlPathHelper urlPathHelper = new UrlPathHelper();

    /**
     * Checks whether this user is authenticated or not.
     *
     * @return this user is authenticated or not
     */
    @Bean
    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass()))
            return false;
        return authentication.isAuthenticated();
    }

    /**
     * Handles page intervention and redirects to login page.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param handler  Object
     * @return this request should be further processed by a handler (true) or not (false).
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("/".equals(urlPathHelper.getLookupPathForRequest(request)) && isAuthenticated()) {
            String encodedRedirectURL = response.encodeRedirectURL(request.getContextPath() + "/dashboard");
            response.setStatus(HttpStatus.TEMPORARY_REDIRECT.value());
            response.setHeader("Location", encodedRedirectURL);
            return false;
        }
        return true;
    }
}