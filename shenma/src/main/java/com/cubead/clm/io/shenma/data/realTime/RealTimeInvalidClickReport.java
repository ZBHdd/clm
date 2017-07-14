package com.cubead.clm.io.shenma.data.realTime;


public class RealTimeInvalidClickReport {
	private String time;
	private Long beforeFilterClickNum;
	private Long afterFilterClickNum;
	private Float filterMoney;
	public RealTimeInvalidClickReport() {}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public Long getBeforeFilterClickNum() {
		return beforeFilterClickNum;
	}
	public void setBeforeFilterClickNum(Long beforeFilterClickNum) {
		this.beforeFilterClickNum = beforeFilterClickNum;
	}
	public Long getAfterFilterClickNum() {
		return afterFilterClickNum;
	}
	public void setAfterFilterClickNum(Long afterFilterClickNum) {
		this.afterFilterClickNum = afterFilterClickNum;
	}
	public Float getFilterMoney() {
		return filterMoney;
	}
	public void setFilterMoney(Float filterMoney) {
		this.filterMoney = filterMoney;
	}
	
}
