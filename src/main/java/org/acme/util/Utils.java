package org.acme.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
	private Utils() {}

	/**
	 * Transforms a string with numbers separated width "," to a ArrayList<Integer>
	 * @param text
	 * @return
	 */
	public static List<Integer> text2IntArray(String text) {
		List<Integer> numberList = new ArrayList<>();

		if(text != null) {
			String[] values = text.split(",");

			for (String value : values) {
				numberList.add(Integer.parseInt(value));
			}
		}

		return numberList;
	}

	/**
	 *
	 * @param str
	 * @return
	 */
	public static String capitalize(String str) {
		if (str == null || str.isEmpty()) {
			return "";
		}
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	/**
	 * Returns true if parameter is a valid email
	 * @param email
	 * @return
	 */
	public static boolean isValidEmail(String email) {
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);

		if (email == null) {
			return false;
		}

		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * Returns the parameter or empty string if it's null
	 * @param text
	 * @return
	 */
	public static String notNullString(String text) {
		if(text == null) {
			return "";
		}
		return text;
	}

	/**
	 * Returns true if parameter is a valid number
	 * @param str
	 * @return
	 */
	public static boolean isValidNumber(String str) {
		if (str == null || str.isEmpty()) {
			return false;
		}

		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isAlpha(String str) {
		return str != null && str.matches("[a-zA-Z]+");
	}
	public static boolean isAlphanumeric(String str) {
		return str != null && str.matches("[a-zA-Z0-9]+");
	}

	public static String formatDate(Date d) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		return format.format(d);
	}
}
