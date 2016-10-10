package com.trinfo.wunderlist.entity;

/**
 * 用户实体类
 * @author Silocean
 *
 */
public class User {
	
	private String userSID;
	private String userEmail;
	private String userName;
	private String userNameEn;
	private String userImageUrl;
	private String userPassword;
	private String userSex;
	private String userAge;
	private String userHobby;
	private String userMemo;
	private String userMobile;
	public String getUserSID() {
		return userSID;
	}
	public void setUserSID(String userSID) {
		this.userSID = userSID;
	}
	public String getUserEmail() {
		return userEmail;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNameEn() {
		return userNameEn;
	}
	public void setUserNameEn(String userNameEn) {
		this.userNameEn = userNameEn;
	}
	public String getUserImageUrl() {
		return userImageUrl;
	}
	public void setUserImageUrl(String userImageUrl) {
		this.userImageUrl = userImageUrl;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserSex() {
		return userSex;
	}
	public void setUserSex(String userSex) {
		this.userSex = userSex;
	}
	public String getUserAge() {
		return userAge;
	}
	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}
	public String getUserHobby() {
		return userHobby;
	}
	public void setUserHobby(String userHobby) {
		this.userHobby = userHobby;
	}
	public String getUserMemo() {
		return userMemo;
	}
	public void setUserMemo(String userMemo) {
		this.userMemo = userMemo;
	}
	public String getUserMobile() {
		return userMobile;
	}
	public void setUserMobile(String userMobile) {
		this.userMobile = userMobile;
	}
	@Override
	public String toString() {
		return "User [userSID=" + userSID + ", userEmail=" + userEmail
				+ ", userName=" + userName + ", userNameEn=" + userNameEn
				+ ", userImageUrl=" + userImageUrl + ", userPassword="
				+ userPassword + ", userSex=" + userSex + ", userAge="
				+ userAge + ", userHobby=" + userHobby + ", userMemo="
				+ userMemo + ", userMobile=" + userMobile + "]";
	}
	
}
