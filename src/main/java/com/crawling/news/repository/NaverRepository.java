package com.crawling.news.repository;

import com.crawling.news.model.NewsNaver;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.stream.Stream;

public interface NaverRepository extends ElasticsearchRepository<NewsNaver, String> {
    @Query("{\n" +
            "    \"query_string\": {\n" +
            "      \"query\": \"?0\",\n" +
            "      \"default_field\": \"url.keyword\",\n" +
            "      \"escape\": true\n" +
            "    }\n" +
            "  }")
    List<NewsNaver> findByUrl(String url);

    Stream<NewsNaver> findAllByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(String gte, String lte);
}