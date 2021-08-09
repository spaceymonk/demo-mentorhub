package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;


@RequestMapping("/api/search")
@RestController
@AllArgsConstructor
public class ApiSearch {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    static class SearchHitResponse {
        private String id;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<List<MentorshipRequest>> findMentorship(@RequestParam("searchTxt") String searchTxt) {

        System.out.println("incoming search text: " + searchTxt);

        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(matchQuery("text", searchTxt)
                        .fuzziness(Fuzziness.ONE))
                .build();

        System.out.println("query created: " + searchQuery);

        SearchHits<SearchHitResponse> searchHits = elasticsearchRestTemplate
                .search(searchQuery, SearchHitResponse.class, IndexCoordinates.of("mentorhub.mentorshiprequest"));

        System.out.println("search hits acquired: " + searchHits);

        List<MentorshipRequest> result = new ArrayList<>();
        searchHits.getSearchHits().forEach(hit -> {
            result.add(mentorshipRequestRepository.findById(Objects.requireNonNull(hit.getId())).orElse(null));
        });

        return ResponseEntity.ok(result);
    }

}
