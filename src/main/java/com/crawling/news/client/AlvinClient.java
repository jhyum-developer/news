package com.crawling.news.client;

import com.crawling.news.model.NewsKeyword;
import com.crawling.news.model.NewsNaver;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(value="com.crawling.news.client.AlvinClient", url = "${alvin.host}")
public interface AlvinClient {

    @GetMapping(path = "/api/news/naver/keyword")
    List<NewsKeyword> getNewsKeywordList();

    @PostMapping(path = "/api/news/naver/save")
    void saveNaverNewsList(@RequestHeader("crudToken") String crudToken,
                           @RequestBody List<NewsNaver> news);
}
