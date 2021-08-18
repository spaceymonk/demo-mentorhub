package com.spaceymonk.mentorhub.controller.api;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import java.util.*;


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
public class ApiSearch {

    private ElasticsearchRestTemplate elasticsearchRestTemplate;
    private MentorshipRequestRepository mentorshipRequestRepository;

    /**
     * Sets elasticsearch rest template.
     *
     * @param elasticsearchRestTemplate the elasticsearch rest template
     */
    @Autowired
    public void setElasticsearchRestTemplate(ElasticsearchRestTemplate elasticsearchRestTemplate) {
        this.elasticsearchRestTemplate = elasticsearchRestTemplate;
    }

    /**
     * Sets mentorship request repository.
     *
     * @param mentorshipRequestRepository the mentorship request repository
     */
    @Autowired
    public void setMentorshipRequestRepository(MentorshipRequestRepository mentorshipRequestRepository) {
        this.mentorshipRequestRepository = mentorshipRequestRepository;
    }

    private List<Map<String, Object>> generateResponse(Query query) {

        SearchHits<SearchHitResponse> searchHits = null;
        try {
            searchHits = elasticsearchRestTemplate
                    .search(query, SearchHitResponse.class, IndexCoordinates.of("mentorhub.mentorshiprequest"));
        } catch (Exception e) {
            return new ArrayList<>();
        }

        List<Map<String, Object>> result = new ArrayList<>();
        searchHits.getSearchHits().forEach(hit -> {
            MentorshipRequest mentorshipRequest = mentorshipRequestRepository.findById(Objects.requireNonNull(hit.getId())).orElse(null);
            assert mentorshipRequest != null;
            if (mentorshipRequest.getStatus().equals("accepted")) {
                Map<String, Object> map = new HashMap<>();
                map.put("majorSubjectName", mentorshipRequest.getSelectedSubject().getMajorSubject());
                map.put("selectedSubjectNames", mentorshipRequest.getSelectedSubject().getSubjects());
                map.put("mentorName", mentorshipRequest.getMentor().getActualName());
                map.put("mentorshipRequestId", mentorshipRequest.getId());
                map.put("text", mentorshipRequest.getText());
                result.add(map);
            }
        });

        return result;
    }

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
    public ResponseEntity<List<Map<String, Object>>> findMentorshipByText(@RequestParam("searchTxt") String searchTxt) {

        if (searchTxt.isBlank())
            return ResponseEntity.ok().build();

        QueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("selectedSubject.majorSubject", searchTxt).fuzziness(Fuzziness.ONE))
                .should(QueryBuilders.matchQuery("selectedSubject.subjects", searchTxt).fuzziness(Fuzziness.ONE))
                .should(QueryBuilders.matchQuery("text", searchTxt).fuzziness(Fuzziness.ONE))
                .filter(QueryBuilders.termQuery("status", "accepted"));
        Query query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        return ResponseEntity.ok(generateResponse(query));
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
    public ResponseEntity<List<Map<String, Object>>> findMentorshipByFilter(@RequestParam("majorSubjectName") String majorSubjectName,
                                                                            @RequestParam("subjectList") List<String> subjectList) {


        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("selectedSubject.majorSubject.keyword", majorSubjectName))
                .must(QueryBuilders.termsQuery("selectedSubject.subjects.keyword", subjectList))
                .filter(QueryBuilders.termQuery("status", "accepted"));

        Query query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        return ResponseEntity.ok(generateResponse(query));
    }

    /**
     * Inner class which used as a layer for Elasticsearch results. <br>
     * It consists of a single field, <code>id</code>. This id then used for data retrieval
     * from the actual database.
     */
    @Data
    @NoArgsConstructor
    private static class SearchHitResponse {
        private String id;
    }
}
