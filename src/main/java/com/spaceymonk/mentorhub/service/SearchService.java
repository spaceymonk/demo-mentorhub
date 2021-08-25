package com.spaceymonk.mentorhub.service;

import com.spaceymonk.mentorhub.domain.MentorshipRequest;
import com.spaceymonk.mentorhub.repository.MentorshipRequestRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
@AllArgsConstructor
public class SearchService {

    private final ElasticsearchRestTemplate elasticsearchRestTemplate;
    private final MentorshipRequestRepository mentorshipRequestRepository;

    private List<Map<String, Object>> generateResponse(Query query) {

        SearchHits<SearchHitResponse> searchHits;
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

    public List<Map<String, Object>> findMentorshipByText(String searchTxt) {
        if (searchTxt.isBlank())
            return new ArrayList<>();

        QueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("selectedSubject.majorSubject", searchTxt).fuzziness(Fuzziness.ONE))
                .should(QueryBuilders.matchQuery("selectedSubject.subjects", searchTxt).fuzziness(Fuzziness.ONE))
                .should(QueryBuilders.matchQuery("text", searchTxt).fuzziness(Fuzziness.ONE))
                .filter(QueryBuilders.termQuery("status", "accepted"));
        Query query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        return generateResponse(query);
    }

    public List<Map<String, Object>> findMentorshipByFilter(String majorSubjectName, List<String> subjectList) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery()
                .must(QueryBuilders.matchQuery("selectedSubject.majorSubject.keyword", majorSubjectName))
                .must(QueryBuilders.termsQuery("selectedSubject.subjects.keyword", subjectList))
                .filter(QueryBuilders.termQuery("status", "accepted"));

        Query query = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();

        return generateResponse(query);
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
