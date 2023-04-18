package com.crawling.news.scheduler;

import com.crawling.news.client.AlvinClient;
import com.crawling.news.model.NewsKeyword;
import com.crawling.news.service.CrawlingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component("com.crawling.news.scheduler.NaverNewsScheduler")
public class NaverNewsScheduler {

    private final AlvinClient alvinClient;

    private final CrawlingService crawlingService;

    public NaverNewsScheduler(AlvinClient alvinClient, CrawlingService crawlingService) {
        this.alvinClient = alvinClient;
        this.crawlingService = crawlingService;
    }


    @Scheduled(cron = "0 0 0,4,7,8,12,16,20 * * ?")
    public void doNaverCrawling() {
        List<NewsKeyword> keywords = this.alvinClient.getNewsKeywordList();
        String gte = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String lte = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        this.crawlingService.crawlingNaver(keywords, gte, lte);

    }
}
