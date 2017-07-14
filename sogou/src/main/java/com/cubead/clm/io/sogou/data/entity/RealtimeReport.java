package com.cubead.clm.io.sogou.data.entity;

import java.util.Date;

public class RealtimeReport {
	private Date date;
	private String account;
	private Double cost;
	private Double cpc;
	private Long click;
	
	public RealtimeReport() {}
	
	public RealtimeReport(Date date, String account, Double cost, Double cpc, Long click) {
		super();
		this.date = date;
		this.account = account;
		this.cost = cost;
		this.cpc = cpc;
		this.click = click;
	}

	public Date getDate() {
		return date;
	}

	public String getAccount() {
		return account;
	}

	public Double getCost() {
		return cost;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	
	public void setAccount(String account) {
		this.account = account;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public Double getCpc() {
		return cpc;
	}

	public void setCpc(Double cpc) {
		this.cpc = cpc;
	}

	public Long getClick() {
		return click;
	}

	public void setClick(Long click) {
		this.click = click;
	}
}