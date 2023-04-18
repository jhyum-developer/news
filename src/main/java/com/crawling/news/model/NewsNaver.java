package com.crawling.news.model;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "news_naver")
public class NewsNaver extends News {
	private String providerName;
	private String naverUrl;

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getNaverUrl() {
		return naverUrl;
	}

	public void setNaverUrl(String naverUrl) {
		this.naverUrl = naverUrl;
	}

	@Override
	public String toString() {
		return "NewsNaver [category=" + category + ", content=" + content + ", createTime=" + createTime + ", domain="
				+ domain + ", keyword=" + keyword + ", provider=" + provider + ", summary=" + summary + ", thumbnail="
				+ thumbnail + ", title=" + title + ", url=" + url + ",naverUrl=" + naverUrl + ", providerName=" + providerName + "]";
	}
	
}