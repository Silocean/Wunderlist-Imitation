package com.wunderlist.tools;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import com.wunderlist.entity.Task;

public class SubjectComparator implements Comparator<Task> {

	@Override
	public int compare(Task lhs, Task rhs) {
		String[] str = new String[]{lhs.getSubject(), rhs.getSubject()};
		String[] strOrignal = new String[2];
		for(int i=0; i<str.length; i++) {
			strOrignal[i] = str[i];
		}
		Comparator<Object> comparator = Collator.getInstance(Locale.CHINA);
		Arrays.sort(str, comparator);
		if(strOrignal[0].equals(str[0]) && strOrignal[1].equals(str[1])) {
			return -1;
		} else {
			return 1;
		}
	}

}
