package com.crawling.news.service;


import com.crawling.news.model.NewsNaver;
import com.crawling.news.repository.NaverRepository;
import com.crawling.news.scheduler.NaverNewsScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service("com.crawling.news.service.NaverService")
public class NaverService {

    private final NaverRepository repository;

    private final NaverNewsScheduler scheduler;

    public NaverService(NaverRepository repository, NaverNewsScheduler scheduler) {
        this.repository = repository;
        this.scheduler = scheduler;
    }

    public List<NewsNaver> getNaverNewsList(String gte, String lte) {
        return this.repository.findAllByCreateTimeGreaterThanEqualAndCreateTimeLessThanEqual(gte, lte)
                .collect(Collectors.toList());
    }

    public void crawlingNaverNews(String gte, String lte) {
        scheduler.doNaverCrawling();
    }
}
