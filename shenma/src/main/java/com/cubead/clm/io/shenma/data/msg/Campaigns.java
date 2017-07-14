package com.cubead.clm.io.shenma.data.msg;

public class Campaigns {
	private Long campaignId;
	private String campaignName;
	private Float budget;
	private String regionTarget;
	private String excludeIp;
	private String negativeWords;
	private String exactNegativeWords;
	private String schedule;
	private Long showProb;
	private Boolean pause;
	private Integer status;
	public Campaigns() {
		
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
	
	public Float getBudget() {
		return budget;
	}
	public void setBudget(Float budget) {
		this.budget = budget;
	}
	public String getRegionTarget() {
		return regionTarget;
	}
	public void setRegionTarget(String regionTarget) {
		this.regionTarget = regionTarget;
	}
	public String getExcludeIp() {
		return excludeIp;
	}
	public void setExcludeIp(String excludeIp) {
		this.excludeIp = excludeIp;
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
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	public Long getShowProb() {
		return showProb;
	}
	public void setShowProb(Long showProb) {
		this.showProb = showProb;
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
