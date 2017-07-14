package com.cubead.clm.io.sogou.data.entity;

import java.util.Date;

public class KeyWordShowNoClickReport {
	private Date date;
	private String account;
	private Long planId;
	private String plan;
	private Long groupId;
	private String group;
	private Long keyWordId;
	private String keyWord;
	private Double cost;
	private Double avgClickPrice;
	private Long clickNum;
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
	public Long getKeyWordId() {
		return keyWordId;
	}
	public void setKeyWordId(Long keyWordId) {
		this.keyWordId = keyWordId;
	}
	public String getKeyWord() {
		return keyWord;
	}
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
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
	
}
