package org.acme.util;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static final List<Integer> text2IntArray(String text) {
		List<Integer> numberList = new ArrayList<>();

		if(text != null) {
			String[] values = text.split(",");

			for (String value : values) {
				numberList.add(Integer.parseInt(value));
			}
		}

		return numberList;
	}

	public static String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return str;
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}
}
