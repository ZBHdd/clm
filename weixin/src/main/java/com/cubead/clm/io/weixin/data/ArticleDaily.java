package com.cubead.clm.io.weixin.data;

// Generated 2017-5-20 18:03:09 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * ArticleDaily generated by hbm2java
 */
@Entity
@Table(name = "article_daily")
public class ArticleDaily implements java.io.Serializable {

	private long id;
	private String appid;
	private Date refDate;
	private String msgid;
	private String title;
	private Integer intPageReadUser;
	private Integer intPageReadCount;
	private Integer oriPageReadUser;
	private Integer oriPageReadCount;
	private Integer shareUser;
	private Integer shareCount;
	private Integer addToFavUser;
	private Integer addToFavCount;

	public ArticleDaily() {
	}

	public ArticleDaily(long id, String appid) {
		this.id = id;
		this.appid = appid;
	}

	public ArticleDaily(long id, String appid, Date refDate, String msgid,
			String title, Integer intPageReadUser, Integer intPageReadCount,
			Integer oriPageReadUser, Integer oriPageReadCount,
			Integer shareUser, Integer shareCount, Integer addToFavUser,
			Integer addToFavCount) {
		this.id = id;
		this.appid = appid;
		this.refDate = refDate;
		this.msgid = msgid;
		this.title = title;
		this.intPageReadUser = intPageReadUser;
		this.intPageReadCount = intPageReadCount;
		this.oriPageReadUser = oriPageReadUser;
		this.oriPageReadCount = oriPageReadCount;
		this.shareUser = shareUser;
		this.shareCount = shareCount;
		this.addToFavUser = addToFavUser;
		this.addToFavCount = addToFavCount;
	}

	@Id
	@SequenceGenerator(name="article_daily_sequence",sequenceName="article_daily_id_seq", allocationSize=1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="article_daily_sequence")	
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "appid", nullable = false)
	public String getAppid() {
		return this.appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "ref_date", length = 13)
	public Date getRefDate() {
		return this.refDate;
	}

	public void setRefDate(Date refDate) {
		this.refDate = refDate;
	}

	@Column(name = "msgid")
	public String getMsgid() {
		return this.msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	@Column(name = "title")
	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Column(name = "int_page_read_user")
	public Integer getIntPageReadUser() {
		return this.intPageReadUser;
	}

	public void setIntPageReadUser(Integer intPageReadUser) {
		this.intPageReadUser = intPageReadUser;
	}

	@Column(name = "int_page_read_count")
	public Integer getIntPageReadCount() {
		return this.intPageReadCount;
	}

	public void setIntPageReadCount(Integer intPageReadCount) {
		this.intPageReadCount = intPageReadCount;
	}

	@Column(name = "ori_page_read_user")
	public Integer getOriPageReadUser() {
		return this.oriPageReadUser;
	}

	public void setOriPageReadUser(Integer oriPageReadUser) {
		this.oriPageReadUser = oriPageReadUser;
	}

	@Column(name = "ori_page_read_count")
	public Integer getOriPageReadCount() {
		return this.oriPageReadCount;
	}

	public void setOriPageReadCount(Integer oriPageReadCount) {
		this.oriPageReadCount = oriPageReadCount;
	}

	@Column(name = "share_user")
	public Integer getShareUser() {
		return this.shareUser;
	}

	public void setShareUser(Integer shareUser) {
		this.shareUser = shareUser;
	}

	@Column(name = "share_count")
	public Integer getShareCount() {
		return this.shareCount;
	}

	public void setShareCount(Integer shareCount) {
		this.shareCount = shareCount;
	}

	@Column(name = "add_to_fav_user")
	public Integer getAddToFavUser() {
		return this.addToFavUser;
	}

	public void setAddToFavUser(Integer addToFavUser) {
		this.addToFavUser = addToFavUser;
	}

	@Column(name = "add_to_fav_count")
	public Integer getAddToFavCount() {
		return this.addToFavCount;
	}

	public void setAddToFavCount(Integer addToFavCount) {
		this.addToFavCount = addToFavCount;
	}

}
