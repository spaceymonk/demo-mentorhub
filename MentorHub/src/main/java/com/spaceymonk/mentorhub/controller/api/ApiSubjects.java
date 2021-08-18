package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ApiSubjects {

    private SubjectRepository subjectRepository;

    /**
     * Sets subject repository.
     *
     * @param subjectRepository the subject repository
     */
    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

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

        // Data Control
        if (subject.getMajorSubject() == null || subject.getMajorSubject().isBlank()
                || subject.getSubjects() == null || subject.getSubjects().removeIf(String::isBlank) || subject.getSubjects().isEmpty()) {
            return ResponseEntity.badRequest().body("No subjects entered!");
        }

        // Data creation
        subjectRepository.save(subject);
        return ResponseEntity.ok(subject.getId());
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
        subjectRepository.deleteById(id);
        return ResponseEntity.ok(id);
    }
}
