package com.cubead.clm.io.shenma.data.msg;

public class Adgroups {
	private Long campaignId;
	private String campaignName;
	private Long adgroupId;
	private String adgroupName;
	private Float maxPrice;
	private Long adPlatformOS;
	private String negativeWords;
	private String exactNegativeWords;
	private Boolean pause;
	private Integer status;
	public Adgroups() {
		
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
	public Float getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(Float maxPrice) {
		this.maxPrice = maxPrice;
	}
	
	public Long getAdPlatformOS() {
		return adPlatformOS;
	}
	public void setAdPlatformOS(Long adPlatformOS) {
		this.adPlatformOS = adPlatformOS;
	}
	public String getNegativeWords() {
		return negativeWords;
	}
	public void setNegativeWords(String negativeWords) {
		this.negativeWords = negativeWords;
	}
	public String getExactNegativeWords() {
		return exactNegativeWords;
	}
	public void setExactNegativeWords(String exactNegativeWords) {
		this.exactNegativeWords = exactNegativeWords;
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
	
	
	
	
}
