package com.cubead.clm.io.shenma.data.msg;

public class Apps {
	private Long campaignId;
	private String campaignName;
	private Long adgroupId;
	private String adgroupName;
	private Long appId;
	private String appName;
	private String appLogo;
	private String downloadAddrIOS;
	private String downloadAddrAndroid;
	private String detailAddrAndroid;
	private Boolean pause;
	private Integer status;
	private String negativeReason;
	public Apps() {
	
	}
	
	public long getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(long campaignId) {
		this.campaignId = campaignId;
	}

	public String getCampaignName() {
		return campaignName;
	}
	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}
	public long getAdgroupId() {
		return adgroupId;
	}
	public void setAdgroupId(long adgroupId) {
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

	public Boolean getPause() {
		return pause;
	}

	public void setPause(Boolean pause) {
		this.pause = pause;
	}

	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}

	public void setAdgroupId(Long adgroupId) {
		this.adgroupId = adgroupId;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAppLogo() {
		return appLogo;
	}
	public void setAppLogo(String appLogo) {
		this.appLogo = appLogo;
	}
	public String getDownloadAddrIOS() {
		return downloadAddrIOS;
	}
	public void setDownloadAddrIOS(String downloadAddrIOS) {
		this.downloadAddrIOS = downloadAddrIOS;
	}
	public String getDownloadAddrAndroid() {
		return downloadAddrAndroid;
	}
	public void setDownloadAddrAndroid(String downloadAddrAndroid) {
		this.downloadAddrAndroid = downloadAddrAndroid;
	}
	public String getDetailAddrAndroid() {
		return detailAddrAndroid;
	}
	public void setDetailAddrAndroid(String detailAddrAndroid) {
		this.detailAddrAndroid = detailAddrAndroid;
	}
	public boolean isPause() {
		return pause;
	}
	public void setPause(boolean pause) {
		this.pause = pause;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getNegativeReason() {
		return negativeReason;
	}
	public void setNegativeReason(String negativeReason) {
		this.negativeReason = negativeReason;
	}
	@Override
	public String toString() {
		return "Apps [campaignId=" + campaignId + ", campaignName=" + campaignName + ", adgroupId=" + adgroupId
				+ ", adgroupName=" + adgroupName + ", appId=" + appId + ", appName=" + appName + ", appLogo=" + appLogo
				+ ", downloadAddrIOS=" + downloadAddrIOS + ", downloadAddrAndroid=" + downloadAddrAndroid
				+ ", detailAddrAndroid=" + detailAddrAndroid + ", pause=" + pause + ", status=" + status
				+ ", negativeReason=" + negativeReason + "]";
	}
	
}
