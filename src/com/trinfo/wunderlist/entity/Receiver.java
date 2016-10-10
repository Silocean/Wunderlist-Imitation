package com.trinfo.wunderlist.entity;

/**
 * 接收人实体类
 * @author Silocean
 *
 */
public class Receiver {
	
	private String receiverId = "";
	private String receiverEmail = "";
	private String receiverName = "";
	private String receiverSex = "";
	private String receiverAge = "";
	private String receiverHobby = "";
	private String receiverMobile = "";
	
	public Receiver() {
	}
	public Receiver(String receiverId, String receiverEmail, String receiverName,
			String receiverSex, String receiverAge, String receiverHobby,
			String receiverMobile) {
		this.receiverId = receiverId;
		this.receiverEmail = receiverEmail;
		this.receiverName = receiverName;
		this.receiverSex = receiverSex;
		this.receiverAge = receiverAge;
		this.receiverHobby = receiverHobby;
		this.receiverMobile = receiverMobile;
	}
	public String getReceiverEmail() {
		return receiverEmail;
	}
	public void setReceiverEmail(String receiverEmail) {
		this.receiverEmail = receiverEmail;
	}
	public String getReceiverName() {
		return receiverName;
	}
	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}
	public String getReceiverSex() {
		return receiverSex;
	}
	public void setReceiverSex(String receiverSex) {
		this.receiverSex = receiverSex;
	}
	public String getReceiverAge() {
		return receiverAge;
	}
	public void setReceiverAge(String receiverAge) {
		this.receiverAge = receiverAge;
	}
	public String getReceiverHobby() {
		return receiverHobby;
	}
	public void setReceiverHobby(String receiverHobby) {
		this.receiverHobby = receiverHobby;
	}
	public String getReceiverMobile() {
		return receiverMobile;
	}
	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}
	public String getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}
	
	@Override
	public String toString() {
		return "Receiver [receiverId=" + receiverId + ", receiverEmail="
				+ receiverEmail + ", receiverName=" + receiverName
				+ ", receiverSex=" + receiverSex + ", receiverAge="
				+ receiverAge + ", receiverHobby=" + receiverHobby
				+ ", receiverMobile=" + receiverMobile + "]";
	}
	
}
