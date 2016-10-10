package com.wunderlist.entity;

public class Group {
	
	private String groupName;
	private int groupTaskCount;
	
	public Group(String groupName, int groupTaskCount) {
		this.groupName = groupName;
		this.groupTaskCount = groupTaskCount;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getGroupTaskCount() {
		return groupTaskCount;
	}

	public void setGroupTaskCount(int groupTaskCount) {
		this.groupTaskCount = groupTaskCount;
	}

}
