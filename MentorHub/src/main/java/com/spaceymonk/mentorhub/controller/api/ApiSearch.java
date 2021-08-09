package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.Mentorship;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/search")
@RestController
@AllArgsConstructor
public class ApiSearch {

    @RequestMapping(value = "/{searchTxt}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Mentorship> findMentorship(@PathVariable("searchTxt") String searchTxt) {
        return ResponseEntity.ok().build();
    }

}
