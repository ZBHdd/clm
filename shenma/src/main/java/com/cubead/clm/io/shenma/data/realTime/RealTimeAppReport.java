package com.cubead.clm.io.shenma.data.realTime;


public class RealTimeAppReport {
	private String time;
	private Long accountId;
	private String accountName;
	private Long campaignId;
	private String campaignName;
	private Long adgroupId;
	private String adgroupName;
	private Long appId;
	private String appName;
	private Long showNum;
	private Long clickNum;
	private Float cost;
	private String clickRate;
	private Float avgClickPrice;
	
	public RealTimeAppReport() {
		
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

	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
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
