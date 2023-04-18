package com.crawling.news;

import com.crawling.news.client.AlvinClient;
import com.crawling.news.model.NewsKeyword;
import com.crawling.news.model.NewsNaver;
import com.crawling.news.service.CrawlingService;
import com.crawling.news.service.NaverService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@SpringBootTest(properties={"spring.profiles.active=local"})
class NewsApplicationTests {

	@Autowired
	private CrawlingService crawlingService;

	@Autowired
	private NaverService naverService;

	@Autowired
	private AlvinClient client;

	@Test
	void crawlingTest() {
		List<NewsKeyword> keywords = client.getNewsKeywordList();
		String gte = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		String lte = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
		crawlingService.crawlingNaver(keywords, gte, lte);
	}

	@Test
	void externalApiTest() {
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String gte = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
		String lte = LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		List<NewsNaver> list = naverService.getNaverNewsList(gte, lte);
	}

}
