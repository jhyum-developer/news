package com.crawling.news.model;

public class NewsKeyword {

	private String uuid;
	private String keyword;
	private String type;

	public NewsKeyword(){

	}

	public NewsKeyword(String uuid){
		this.uuid = uuid;
	}

	public NewsKeyword(String type, String keyword){
		this.type = type;
		this.keyword = keyword;
	}

	public NewsKeyword(String uuid, String type, String keyword){
		this.uuid = uuid;
		this.type = type;
		this.keyword = keyword;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "NewsKeyword [keyword=" + keyword + ", type=" + type + ", uuid=" + uuid + "]";
	}

}