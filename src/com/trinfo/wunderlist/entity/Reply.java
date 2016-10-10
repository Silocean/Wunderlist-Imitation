package com.trinfo.wunderlist.entity;

import java.io.Serializable;

/**
 * 回复实体类
 * @author Silocean
 *
 */
public class Reply implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String replyId;
	private String taskId;
	private String userId;
	private String userEmail;
	private String replyContent;
	private String createDate;
	public String getReplyId() {
		return replyId;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
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
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() {
		return "Reply [replyId=" + replyId + ", taskId=" + taskId + ", userId="
				+ userId + ", userEmail=" + userEmail + ", replyContent="
				+ replyContent + ", createDate=" + createDate + "]";
	}

}
