package com.crawling.news.service;

import com.crawling.news.client.AlvinClient;
import com.crawling.news.component.NewsCrawler;
import com.crawling.news.model.NewsKeyword;
import com.crawling.news.model.NewsNaver;
import com.crawling.news.repository.NaverRepository;
import com.crawling.news.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service("com.crawling.news.service.CrawlingService")
public class CrawlingService {

    private final NewsCrawler newsCrawler;

    private final NaverRepository naverRepository;

    private final AlvinClient alvinClient;

    private static Logger logger = LoggerFactory.getLogger(CrawlingService.class);

    public CrawlingService(NewsCrawler newsCrawler, NaverRepository naverRepository, AlvinClient alvinClient) {
        this.newsCrawler = newsCrawler;
        this.naverRepository = naverRepository;
        this.alvinClient = alvinClient;
    }

    public NewsNaver addNewsNaver(NewsNaver news) {
        logger.debug("SAVE NEWS NAVER: " + news.getUrl());
        return naverRepository.save(news);
    }

    public List<NewsNaver> saveAllNaverNews(List<NewsNaver> news) {
        logger.debug("Saved Naver news count: " + news.size());

        return (List<NewsNaver>) naverRepository.saveAll(news);
    }

    public void crawlingNaver(List<NewsKeyword> keywords, String gte, String lte) {
        for (NewsKeyword k : keywords) {
            try {
                List<NewsNaver> result = newsCrawler.crawlNaver(k.getKeyword(), gte, lte).stream()
                        .filter(news -> !(news.getTitle().contains("가상화폐") && news.getDomain().contains("www.wowtv.co.kr")))
                        .filter(news -> !(news.getTitle().contains("우리동네 지역전문가") && news.getDomain().contains("www.mk.co.kr")))
                        .filter(news -> !(news.getTitle().contains("크립토 시황") && news.getDomain().contains("www.fnnews.com")))
                        .filter(news -> !(news.getTitle().contains("비트코인 지금") && news.getDomain().contains("www.asiae.co.kr")))
                        .filter(news -> !(news.getContent().contains("A사")))
                        .collect(Collectors.toList());

                /* Arches Elasticsearch Indexing */
                this.saveAllNaverNews(result);
                /* Alvin Elasticsearch Indexing */
                this.alvinClient.saveNaverNewsList(Util.makeCrudToken(), result);

                /* 2 ~ 4Second Delay */
                Random r = new Random();
                long mills = (r.nextInt(4) + 1) * 1000;
                Thread.sleep(mills);
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("Keyword cause exception: " + k);
                continue;
            }
        }
    }
}
