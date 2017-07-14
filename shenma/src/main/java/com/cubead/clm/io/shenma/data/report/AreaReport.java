package com.cubead.clm.io.shenma.data.report;


public class AreaReport {
	private String time;
	private Long accountId;
	private String accountName;
	private Long AreaId;
	private String AreaName;
	private Long showNum;
	private Long clickNum;
	private Float cost;
	private String clickRate;
	private Float avgClickPrice;
	public AreaReport() {
		
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Long getAreaId() {
		return AreaId;
	}
	public void setAreaId(Long areaId) {
		AreaId = areaId;
	}
	public String getAreaName() {
		return AreaName;
	}
	public void setAreaName(String areaName) {
		AreaName = areaName;
	}
	public Long getShowNum() {
		return showNum;
	}
	public void setShowNum(Long showNum) {
		this.showNum = showNum;
	}
	public Long getClickNum() {
		return clickNum;
	}
	public void setClickNum(Long clickNum) {
		this.clickNum = clickNum;
	}
	public Float getCost() {
		return cost;
	}
	public void setCost(Float cost) {
		this.cost = cost;
	}
	public String getClickRate() {
		return clickRate;
	}
	public void setClickRate(String clickRate) {
		this.clickRate = clickRate;
	}
	public Float getAvgClickPrice() {
		return avgClickPrice;
	}
	public void setAvgClickPrice(Float avgClickPrice) {
		this.avgClickPrice = avgClickPrice;
	}
	
}
