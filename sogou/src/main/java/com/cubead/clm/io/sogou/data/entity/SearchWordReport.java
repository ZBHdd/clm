package com.cubead.clm.io.sogou.data.entity;

import java.util.Date;

public class SearchWordReport {
	private Date date;
	private String account;
	private Long planId;
	private String plan;
	private Long groupId;
	private String group;
	private Long ideaId;
	private String idea;
	private String ideaDesc1;
	private String ideaDesc2;
	private String ideaAccessUrl;
	private String ideaShowUrl;
	private String ideaMobilAccessUrl;
	private String ideaMobilShowUrl;
	private String keyWord;
	private String searchWord;
	private Double cost;
	private Double avgClickPrice;
	private Long clickNum;
	private Long showNum;
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public Long getPlanId() {
		return planId;
	}
	public void setPlanId(Long planId) {
		this.planId = planId;
	}
	public String getPlan() {
		return plan;
	}
	public void setPlan(String plan) {
		this.plan = plan;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public Long getIdeaId() {
		return ideaId;
	}
	public void setIdeaId(Long ideaId) {
		this.ideaId = ideaId;
	}
	public String getIdea() {
		return idea;
	}
	public void setIdea(String idea) {
		this.idea = idea;
	}
	public String getIdeaDesc1() {
		return ideaDesc1;
	}
	public void setIdeaDesc1(String ideaDesc1) {
		this.ideaDesc1 = ideaDesc1;
	}
	public String getIdeaDesc2() {
		return ideaDesc2;
	}
	public void setIdeaDesc2(String ideaDesc2) {
		this.ideaDesc2 = ideaDesc2;
	}
	public String getIdeaAccessUrl() {
		return ideaAccessUrl;
	}
	public void setIdeaAccessUrl(String ideaAccessUrl) {
		this.ideaAccessUrl = ideaAccessUrl;
	}
	public String getIdeaShowUrl() {
		return ideaShowUrl;
	}
	public void setIdeaShowUrl(String ideaShowUrl) {
		this.ideaShowUrl = ideaShowUrl;
	}
	public String getIdeaMobilAccessUrl() {
		return ideaMobilAccessUrl;
	}
	public void setIdeaMobilAccessUrl(String ideaMobilAccessUrl) {
		this.ideaMobilAccessUrl = ideaMobilAccessUrl;
	}
	public String getIdeaMobilShowUrl() {
		return ideaMobilShowUrl;
	}
	public void setIdeaMobilShowUrl(String ideaMobilShowUrl) {
		this.ideaMobilShowUrl = ideaMobilShowUrl;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	public String getSearchWord() {
		return searchWord;
	}
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}
	public Double getCost() {
		return cost;
	}
	public void setCost(Double cost) {
		this.cost = cost;
	}
	public Double getAvgClickPrice() {
		return avgClickPrice;
	}
	public void setAvgClickPrice(Double avgClickPrice) {
		this.avgClickPrice = avgClickPrice;
	}
	public Long getClickNum() {
		return clickNum;
	}
	public void setClickNum(Long clickNum) {
		this.clickNum = clickNum;
	}
	public Long getShowNum() {
		return showNum;
	}
	public void setShowNum(Long showNum) {
		this.showNum = showNum;
	}
	
}
