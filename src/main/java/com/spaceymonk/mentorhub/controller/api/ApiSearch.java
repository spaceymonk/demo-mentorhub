package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.List;


/**
 * API class for Search related operations.
 * Handles <code>/api/search/**</code> endpoint.
 * <br/>
 * This API class does below operations:
 * <ul>
 *     <li>Search mentorship requests by text</li>
 *     <li>Search mentorship requests by filter</li>
 * </ul>
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@RequestMapping("/api/search")
@RestController
@AllArgsConstructor
public class ApiSearch {

    public final SearchService searchService;

    /**
     * Find mentorships by text. <br>
     * Search only done in accepted requests. Given text searched in major subject name, subject names
     * and text field of the request.
     *
     * @param searchTxt the search txt
     * @return list of mentorship requests
     */
    @RequestMapping(value = "/method/text", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<Object> findMentorshipByText(@RequestParam("searchTxt") String searchTxt) {
        try {
            var result = searchService.findMentorshipByText(searchTxt);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Find mentorship by filter. <br>
     * Search only done in accepted requests.
     *
     * @param majorSubjectName the major subject name
     * @param subjectList      the subject list
     * @return list of mentorship requests
     */
    @RequestMapping(value = "/method/filter", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<Object> findMentorshipByFilter(@RequestParam("majorSubjectName") String majorSubjectName,
                                                         @RequestParam("subjectList") List<String> subjectList) {
        try {
            var result = searchService.findMentorshipByFilter(majorSubjectName, subjectList);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
