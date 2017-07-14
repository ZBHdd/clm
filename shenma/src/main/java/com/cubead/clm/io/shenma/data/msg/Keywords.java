package com.cubead.clm.io.shenma.data.msg;

public class Keywords {
	private Long campaignId;
	private String campaignName;
	private Long adgroupId;
	private String adgroupName;
	private Long keywordId;
	private String keyword;
	private Float price;
	private String destinationUrl;
	private Integer matchType;
	private Boolean pause;
	private Integer status;
	private Long quality;
	private String negativeReason;
	public Keywords() {
	
	}
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	public Long getAdgroupId() {
		return adgroupId;
	}
	public void setAdgroupId(Long adgroupId) {
		this.adgroupId = adgroupId;
	}
	public String getAdgroupName() {
		return adgroupName;
	}
	public void setAdgroupName(String adgroupName) {
		this.adgroupName = adgroupName;
	}
	public Long getKeywordId() {
		return keywordId;
	}
	public void setKeywordId(Long keywordId) {
		this.keywordId = keywordId;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getDestinationUrl() {
		return destinationUrl;
	}
	public void setDestinationUrl(String destinationUrl) {
		this.destinationUrl = destinationUrl;
	}
	public Integer getMatchType() {
		return matchType;
	}
	public void setMatchType(Integer matchType) {
		this.matchType = matchType;
	}
	public Boolean getPause() {
		return pause;
	}
	public void setPause(Boolean pause) {
		this.pause = pause;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	
	public Long getQuality() {
		return quality;
	}
	public void setQuality(Long quality) {
		this.quality = quality;
	}
	public String getNegativeReason() {
		return negativeReason;
	}
	public void setNegativeReason(String negativeReason) {
		this.negativeReason = negativeReason;
	}
	
	
}
