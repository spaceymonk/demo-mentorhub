package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.*;


@RequestMapping("/api/search")
@RestController
@AllArgsConstructor
public class ApiSearch {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    private List<Map<String, Object>> generateResponse(Query searchQuery) {
        SearchHits<SearchHitResponse> searchHits = elasticsearchRestTemplate
                .search(searchQuery, SearchHitResponse.class, IndexCoordinates.of("mentorhub.mentorshiprequest"));

        List<Map<String, Object>> result = new ArrayList<>();
        searchHits.getSearchHits().forEach(hit -> {
            MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(Objects.requireNonNull(hit.getId())).orElse(null);
            assert mentorshipRequest != null;
            Map<String, Object> map = new HashMap<>();
            map.put("majorSubjectName", mentorshipRequest.getSelectedSubject().getMajorSubject());
            map.put("selectedSubjectNames", mentorshipRequest.getSelectedSubject().getSubjects());
            map.put("mentorName", mentorshipRequest.getMentor().getActualName());
            map.put("mentorshipRequestId", mentorshipRequest.getId());
            map.put("text", mentorshipRequest.getText());
            result.add(map);
        });

        return result;
    }

    @RequestMapping(value = "/method/text", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<Map<String, Object>>> findMentorshipByText(@RequestParam("searchTxt") String searchTxt) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(searchTxt,
                        "text", "selectedSubject.majorSubject", "selectedSubject.subjects"))
                .build();

        return ResponseEntity.ok(generateResponse(searchQuery));
    }

    @RequestMapping(value = "/method/filter", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<Map<String, Object>>> findMentorshipByFilter(@RequestParam("majorSubjectName") String majorSubjectName,
                                                                            @RequestParam("subjectList") List<String> subjectList) {

        Criteria criteria = new Criteria("selectedSubject.majorSubject").is(majorSubjectName)
                .and("selectedSubject.subjects").is(subjectList);
        Query searchQuery = new CriteriaQuery(criteria);

        return ResponseEntity.ok(generateResponse(searchQuery));
    }

    static class SearchHitResponse {
        private String id;
    }

}
