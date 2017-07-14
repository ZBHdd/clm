package com.cubead.clm.io.shenma.data.realTime;


public class RealTimeAccountReport{
	private String time;
	private Long accountID;
	private String account;
	private Long showNum;
	private Long clickNum;
	private Float cost;
	private String clickRate;
	private Float avgClickPrice;
	
	public RealTimeAccountReport() {
		super();
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	
	public Long getAccountID() {
		return accountID;
	}

	public void setAccountID(Long accountID) {
		this.accountID = accountID;
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
