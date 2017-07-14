package com.cubead.clm.io.sogou.data.entity;

import java.util.Date;

public class AccountReport {
	private Date date;
	private String account;
	private Double cost;
	private Double avgClickPrice;
	private Long clickNum;
	private Long showNum;
	private String clickRate;
	private String keyworddAvgSort;
	
	public AccountReport() {}

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

	public String getClickRate() {
		return clickRate;
	}

	public void setClickRate(String clickRate) {
		this.clickRate = clickRate;
	}

	public String getKeyworddAvgSort() {
		return keyworddAvgSort;
	}

	public void setKeyworddAvgSort(String keyworddAvgSort) {
		this.keyworddAvgSort = keyworddAvgSort;
	}
	
	
}
