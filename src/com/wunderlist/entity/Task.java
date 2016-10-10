package com.wunderlist.entity;

public class Task {
	
	private String taskTitle;
	private String taskDetails;
	private String taskTime;
	
	public Task(String title, String details, String time) {
		this.taskTitle = title;
		this.taskDetails = details;
		this.taskTime = time;
	}

	public String getTaskTitle() {
		return taskTitle;
	}

	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}

	public String getTaskDetails() {
		return taskDetails;
	}

	public void setTaskDetails(String taskDetails) {
		this.taskDetails = taskDetails;
	}

	public String getTaskTime() {
		return taskTime;
	}

	public void setTaskTime(String taskTime) {
		this.taskTime = taskTime;
	}


}
