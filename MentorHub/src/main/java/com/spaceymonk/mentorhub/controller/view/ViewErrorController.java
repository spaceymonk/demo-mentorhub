package com.spaceymonk.mentorhub.controller.view;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;


/**
 * The View Error page controller.
 * Renders errors arise while rendering views.
 * Handles <code>/error/**</code> endpoints
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@Controller
@RequestMapping(value = "/error")
public class ViewErrorController implements ErrorController {

    /**
     * Renders Error page according to HTTP status code.
     *
     * @param request the request
     * @return string that represents model and view
     */
    @GetMapping("")
    public String renderErrorPage(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "errors/404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "errors/500";
            }
        }
        return "errors/error";
    }

    /**
     * Renders access denied page.
     *
     * @return the string
     */
    @GetMapping("/403")
    public String renderAccessDeniedPage() {
        return "errors/403";
    }
}
