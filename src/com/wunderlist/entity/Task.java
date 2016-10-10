package com.wunderlist.entity;

import java.io.Serializable;

public class Task implements Serializable {
	
	private String taskId;
	private String userId;
	private String taskFrom;
	private String subject;
	private String disc;
	private String priority;
	private String enddate;
	private String remindtype;
	private String remindnum;
	private String isActive;
	
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
	@Override
	public String toString() {
		return "Task [taskId=" + taskId + ", userId=" + userId + ", taskFrom="
				+ taskFrom + ", subject=" + subject + ", disc=" + disc
				+ ", priority=" + priority + ", enddate=" + enddate
				+ ", remindtype=" + remindtype + ", remindnum=" + remindnum
				+ ", isActive=" + isActive + "]";
	}
}
