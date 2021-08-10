package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import lombok.AllArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.*;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;


@RequestMapping("/api/search")
@RestController
@AllArgsConstructor
public class ApiSearch {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    static class SearchHitResponse {
        private String id;
    }

    private Map<String, Object> generateResponse(MentorshipRequest mentorshipRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("majorSubjectName", mentorshipRequest.getSelectedSubject().getMajorSubject());
        map.put("selectedSubjectNames", mentorshipRequest.getSelectedSubject().getSubjects());
        map.put("mentorName", mentorshipRequest.getMentor().getActualName());
        map.put("mentorshipRequestId", mentorshipRequest.getId());
        map.put("text", mentorshipRequest.getText());
        return map;
    }

    @RequestMapping(value = "/method/text", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<Map<String, Object>>> findMentorshipByText(@RequestParam("searchTxt") String searchTxt) {

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(multiMatchQuery(searchTxt, "text", "selectedSubject.majorSubject", "selectedSubject.subjects")
                        .fuzziness(Fuzziness.ONE))
                .build();


        SearchHits<SearchHitResponse> searchHits = elasticsearchRestTemplate
                .search(searchQuery, SearchHitResponse.class, IndexCoordinates.of("mentorhub.mentorshiprequest"));

        List<Map<String, Object>> result = new ArrayList<>();
        searchHits.getSearchHits().forEach(hit -> {
            MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(Objects.requireNonNull(hit.getId())).orElse(null);
            assert mentorshipRequest != null;
            result.add(generateResponse(mentorshipRequest));
        });

        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/method/filter", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<Map<String, Object>>> findMentorshipByFilter(@RequestParam("majorSubjectName") String majorSubjectName,
                                                                            @RequestParam("subjectList") List<String> subjectList) {

        List<Map<String, Object>> result = new ArrayList<>();

        //todo

        return ResponseEntity.ok(result);
    }

}
