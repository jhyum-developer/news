package com.crawling.news.controller;


import com.crawling.news.model.NewsNaver;
import com.crawling.news.service.NaverService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RequestMapping("/api/news/naver")
@RestController("com.crawling.news.controller.NaverNewsApi")
public class NaverNewsApi {

    private final NaverService service;

    public NaverNewsApi(NaverService service) {
        this.service = service;
    }

    @GetMapping(path = "/list")
    public List<NewsNaver> getNaverNewsList(@RequestParam("gte") String gte,
                                            @RequestParam("lte") String lte) {
        return this.service.getNaverNewsList(gte, lte);
    }

    @GetMapping(path = "/crawling")
    public void crawlingNaverNews(@RequestParam("gte") String gte,
                                  @RequestParam("lte") String lte) {
        service.crawlingNaverNews(gte, lte);
    }
}
