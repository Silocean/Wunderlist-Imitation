package com.wunderlist.tools;

import java.util.Comparator;
import java.util.Date;

public class ClockTimeComparator implements Comparator<Date> {

	@Override
	public int compare(Date lhs, Date rhs) {
		if(lhs.before(rhs)) {
			return -1;
		} else if(lhs.after(rhs)) {
			return 1;
		}
		return 0;
	}

}
