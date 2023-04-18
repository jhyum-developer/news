package com.crawling.news.model;

import org.springframework.data.annotation.Id;

public class News {
	@Id
	String id;
	String url;
	String provider;
	String keyword;
	String title;
	String content;
	String createTime;
	String thumbnail;
	String category;
	String summary;
	String domain;
	String ogImage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getOgImage() {
		return ogImage;
	}

	public void setOgImage(String domain) {
		this.ogImage = domain;
	}

	@Override
	public String toString() {
		return "News [category=" + category + ", content=" + content + ", createTime=" + createTime + ", domain="
				+ domain + ", keyword=" + keyword + ", provider=" + provider + ", summary=" + summary + ", thumbnail="
				+ thumbnail + ", title=" + title + ", url=" + url + ", ogImage=" + ogImage + "]";
	}
}