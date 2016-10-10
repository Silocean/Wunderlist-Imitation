package com.trinfo.wunderlist.entity;

import java.io.Serializable;

/**
 * 任务实体类
 * @author Silocean
 *
 */
public class Task implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String taskId;
	private String userId;
	private String taskFrom;
	private String subject;
	private String disc;
	private String priority;
	private String enddate;
	private String remindtype;
	private String remindnum;
	private String createDate;
	private String isActive;
	
	public Task() {
	}
	
	public Task(String taskId, String userId, String taskFrom, String subject,
			String disc, String priority, String enddate, String remindtype,
			String remindnum, String createDate, String isActive) {
		this.taskId = taskId;
		this.userId = userId;
		this.taskFrom = taskFrom;
		this.subject = subject;
		this.disc = disc;
		this.priority = priority;
		this.enddate = enddate;
		this.remindtype = remindtype;
		this.remindnum = remindnum;
		this.createDate = createDate;
		this.isActive = isActive;
	}
	
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTaskFrom() {
		return taskFrom;
	}
	public void setTaskFrom(String taskFrom) {
		this.taskFrom = taskFrom;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDisc() {
		return disc;
	}
	public void setDisc(String disc) {
		this.disc = disc;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getRemindtype() {
		return remindtype;
	}
	public void setRemindtype(String remindtype) {
		this.remindtype = remindtype;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public String getRemindnum() {
		return remindnum;
	}
	public void setRemindnum(String remindnum) {
		this.remindnum = remindnum;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "Task [taskId=" + taskId + ", userId=" + userId + ", taskFrom="
				+ taskFrom + ", subject=" + subject + ", disc=" + disc
				+ ", priority=" + priority + ", enddate=" + enddate
				+ ", remindtype=" + remindtype + ", remindnum=" + remindnum
				+ ", createDate=" + createDate + ", isActive=" + isActive + "]";
	}
}
