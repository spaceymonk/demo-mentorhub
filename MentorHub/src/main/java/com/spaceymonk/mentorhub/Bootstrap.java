package com.spaceymonk.mentorhub;

import com.spaceymonk.mentorhub.domain.Role;
import com.spaceymonk.mentorhub.domain.Subject;
import com.spaceymonk.mentorhub.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

@Component
@AllArgsConstructor
public class Bootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MentorshipRepository mentorshipRepository;
    private final MentorshipRequestRepository mentorshipRequestRepository;
    private final SubjectRepository subjectRepository;
    private final RoleRepository roleRepository;
    private final ElasticsearchRestTemplate elasticsearchTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Started in bootstrap...");

        Query searchQuery = new NativeSearchQueryBuilder()
                .withFilter(matchQuery("majorSubject", "paradigms"))
                .build();
        SearchHits<Subject> articles = elasticsearchTemplate.search(searchQuery, Subject.class, IndexCoordinates.of("mentorhub.subject"));

        System.out.println(articles);
        System.out.println(articles.getSearchHit(0));


        // setup roles if they don't exist
        String[] roles = {"ROLE_USER", "ROLE_ADMIN"};
        for (String role : roles)
            if (roleRepository.findByName(role) == null)
                roleRepository.save(new Role(role));

    }
}
