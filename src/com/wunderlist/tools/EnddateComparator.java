package com.wunderlist.tools;

import java.util.Comparator;
import java.util.Date;

import com.wunderlist.entity.Task;

public class EnddateComparator implements Comparator<Task> {

	@Override
	public int compare(Task lhs, Task rhs) {
		if(!lhs.getEnddate().equals("") && !rhs.getEnddate().equals("")) {
			Date lhsDate = TimeConvertTool.convertToDate(lhs.getEnddate());
			Date rhsDate = TimeConvertTool.convertToDate(rhs.getEnddate());
			if(lhsDate.before(rhsDate)) {
				return -1;
			} else if(lhsDate.after(rhsDate)) {
				return 1;
			} else {
				return 0;
			}
		} else if(!lhs.getEnddate().equals("") && rhs.getEnddate().equals("")) {
			return 1;
		} else if(lhs.getEnddate().equals("") && !rhs.getEnddate().equals("")) {
			return -1;
		} else  {
			return 0;
		}
	}

}
