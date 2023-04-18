package com.crawling.news.component;

import com.crawling.news.model.NewsNaver;
import com.crawling.news.repository.NaverRepository;
import com.crawling.news.utils.HtmlHelper;
import com.crawling.news.utils.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component("com.crawling.news.component.NewsCrawler")
public class NewsCrawler {

	private final NaverRepository naverRepository;

	private static Logger logger = LoggerFactory.getLogger(NewsCrawler.class);
	@Value("${alvin.crawler.news.timeout}")
	private int timeout;

	public NewsCrawler(NaverRepository naverRepository) {
		this.naverRepository = naverRepository;
	}

	protected String getNaverUri(String keyword, String startDate, String endDate, int startRow) {
        keyword = keyword.replace(" ", "+");
        keyword = keyword.replace("\"", "%22");
		if(startDate != null){
			startDate = "&ds=" + startDate;
		} else {
			startDate = "";
		}
		if(endDate != null){
			endDate = "&de=" + endDate;
		} else {
			endDate = "";
		}

		String idx = "&start=" + startRow;

		return "https://search.naver.com/search.naver?sm=tab_hty.top&where=news&sort=2&pd=3&query=" + keyword + startDate + endDate + idx;
	}

	/**
	 * startDate,endDate yyyy.MM.dd
	 */
	public List<NewsNaver> crawlNaver(String keyword, String startDate, String endDate) throws Exception{
		/*
		네이버 뉴스 사이트 개편 : 2020.11.02
		주요변화 :
		 1. 검색결과 레이아웃 카드형으로 변경
		 2. 언론사사이트 직접링크 -> 네이버뉴스 링크는 별도로 제공
		 */
		List<NewsNaver> news = new ArrayList<NewsNaver>();
		int startRow = 1;
		while(true){
			String uri = getNaverUri(keyword, startDate, endDate, startRow);
			Document listDoc = Jsoup.connect(uri).timeout(timeout)
					.userAgent("Mozilla/5.0")
					.referrer("https://search.naver.com/search.naver")
					.get();

			Elements searchResults = listDoc.select("div > ul.list_news > li.bx");
			for (Element el : searchResults) {
				try {
					String articleHref = null;
					String pressUrl = el.select("div.news_area div.info_group a.press").attr("href");
					String pressHref = el.select("a.news_tit").attr("href");

					/* 중복 체크 */
					List<NewsNaver> found = naverRepository.findByUrl(pressHref);
					if (found.size() > 0) {
						logger.info("Already exist ==> " + pressHref);
						continue;
					}

					String thumbnail = el.select("a.dsc_thumb img").attr("data-lazysrc");
					String title = el.select("a.news_tit").text();
					String summary = el.select("div.dsc_wrap").text();
					String naverDate = el.select("div.news_area div.info_group span.info").text();
					String naverHref = null;
					try {
						naverHref = el.select("div.info_group a").get(1).attr("href");
					} catch (Exception e) {}

					articleHref = naverHref == null? pressHref:naverHref;

					String category = null;
					String content = "";
					String createTime = parseHumanReadableDate(naverDate, "yyyy.MM.dd", ZoneId.of("Asia/Seoul"))
							.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
					//Press Domain
					Document contentDoc = null;

					try {
						contentDoc = Jsoup.connect(articleHref).timeout(timeout).get();

					} catch (IOException e) {
						e.printStackTrace();
						continue;
					}


					category = naverHref != null? contentDoc.select("li[role=tab].is_active").text() : "기타";
					if (category.length() == 0) {
						category = "기타";
					}

					logger.info("scrapping url : " + articleHref);

					String[] tmpDomainToken = pressUrl.split("//");
					String domain = "";
					if (tmpDomainToken.length > 1) {
						domain = tmpDomainToken[1].replace("/", "");
					}

					if (naverHref != null) {
						Element contents =contentDoc.selectFirst("#newsct_article");
						for (Element c : contents.children()) {
							if (c.tagName().toLowerCase().equals("script") ||
									(c.tagName().toLowerCase().equals("strong") && c.attr("class").contains("media_end_summary")) )
								c.remove();
						}
						content = contents.text();
					} else {
						Element elArticle = contentDoc.selectFirst("article");
						Elements elArticleByClass = contentDoc.select("[class*='body']");

						if (elArticle != null) {
							content = elArticle.text();
						} else if (elArticleByClass.size() > 0) {
							content = elArticleByClass.text();
						} else {
							content = contentDoc.select("body").text();
						}
					}

					NewsNaver n = new NewsNaver();
					n.setId(UUID.randomUUID().toString());
					n.setProvider("naver");
					n.setProviderName("네이버");
					n.setKeyword(keyword);
					n.setTitle(title);
					n.setUrl(pressHref);
					n.setNaverUrl(articleHref);
					n.setContent(content);
					n.setCreateTime(createTime);
					n.setThumbnail(thumbnail);
					n.setCategory(category);
					n.setSummary(summary);
					n.setDomain(domain);
					n.setOgImage(HtmlHelper.getOgImage(contentDoc, articleHref));
					news.add(n);

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						continue;
					}
				}
				catch (Exception e) {
					logger.error(e.getMessage());
				}
			}

			Element elNextBtn = listDoc.selectFirst("div.sc_page a.btn_next");
			try {
				if (Objects.isNull(elNextBtn) || elNextBtn.attr("aria-disabled").equals("true")) {
					break;
				}
			} catch (Exception e) {
				logger.error(Util.makeStackTrace(e));
				break;
			}
			startRow += 10;
		}
		
		return news;
	}

	private ZonedDateTime parseHumanReadableDate(String dateStr, String dateTimePattern, ZoneId zoneId) {
		LocalDateTime localDateTime;
		if (dateStr.contains("시간 전") || dateStr.contains("시간전")) {
			String before = pickNumber(dateStr.substring(0, dateStr.indexOf("시간")));
			localDateTime = LocalDateTime.now().minusHours(Integer.parseInt(before));
		} else if (dateStr.contains("분 전") || dateStr.contains("분전")) {
			String before = pickNumber(dateStr.substring(0, dateStr.indexOf("분")));
			localDateTime = LocalDateTime.now().minusMinutes(Integer.parseInt(before));
		} else if (dateStr.contains("초 전") || dateStr.contains("초전")) {
			String before = pickNumber(dateStr.substring(0, dateStr.indexOf("초")));
			localDateTime = LocalDateTime.now().minusSeconds(Integer.parseInt(before));
		} else if (dateStr.contains("일 전") || dateStr.contains("일전")) {
			String before = pickNumber(dateStr.substring(0, dateStr.indexOf("일")));
			localDateTime = LocalDateTime.now().minusDays(Integer.parseInt(before));
		} else {
			LocalDate localDate = LocalDate.parse(dateStr.replaceAll("\\s", ""), DateTimeFormatter.ofPattern(dateTimePattern));
			localDateTime = localDate.atStartOfDay();
		}
		return ZonedDateTime.of(localDateTime, zoneId);
	}

	private String pickNumber(String s) {
		String[] parts = s.trim().split("\\s");
		return parts[parts.length-1];
	}
}