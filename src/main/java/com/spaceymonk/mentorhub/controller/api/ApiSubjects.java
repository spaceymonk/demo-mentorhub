package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.service.SubjectService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;


/**
 * API class for Subject related operations.
 * Handles <code>/api/subjects/**</code> endpoint.
 * <br/>
 * This API class does below operations:
 * <ul>
 *     <li>Create new subjects</li>
 *     <li>Update existing subjects</li>
 *     <li>Remove subjects</li>
 * </ul>
 *
 * @author spaceymonk
 * @version 1.0 08/18/21
 */
@RequestMapping("/api/subjects")
@RestController
@AllArgsConstructor
public class ApiSubjects {

    private final SubjectService subjectService;

    /**
     * Create a new subject or overwrite the existing one.
     * <br>
     * If id field of Subject left <code>null</code> then create a new
     * phase object in the system.
     *
     * @param subject the subject
     * @return created subject id
     */
    @RequestMapping(value = "/", consumes = "application/json", method = RequestMethod.PUT)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> saveSubjectDetails(@RequestBody Subject subject) {
        try {
            String id = subjectService.saveSubjectToDb(subject);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete subject from the system.
     *
     * @param id the id of the subject
     * @return nothing
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @RolesAllowed({"ROLE_ADMIN"})
    public ResponseEntity<String> deleteSubject(@PathVariable("id") String id) {
        try {
            String id_ = subjectService.removeSubjectFromDb(id);
            return ResponseEntity.ok(id_);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
