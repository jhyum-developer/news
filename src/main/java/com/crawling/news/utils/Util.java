package com.crawling.news.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

public class Util {

	public static boolean isNull(Object value) {
        return value == null;
	}
	
	public static boolean isEmpty(String value) {
        return isNull(value) || (value.trim().length() == 0);
	}
	
	public static String makeStackTrace(Throwable t) {
		if (t == null)
			return "";
		try {
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			t.printStackTrace(new PrintStream(bout));
			bout.flush();
			String error = new String(bout.toByteArray());

			return error;
		} catch (Exception ex) {
			return "";
		}
	}

	public static void sleepThread(int baseSecond) {
		Random r = new Random();
		long mills = (r.nextInt(baseSecond) + 1) * 1000;
		try {
			Thread.sleep(mills);
		} catch (Exception e) {}
	}

	public static void main (String[] args) {
		Random r = new Random();
		long mills = (r.nextInt(5) + 1) * 1000;
		System.out.println(mills);
	}

	private static int translateStringtoAscrii(String str) {
		int result = 0;

		for (char ch : str.toCharArray()) {
			result += ch;
		}
		return result;
	}

	private static List<Integer> make_ascii_array(int ascii) {
		List<Integer> result = new ArrayList<>();
		BiFunction<Integer, List<Integer>, Integer> fn = (num, array) -> {
			int o = num / 26;
			int r = num % 26;
			r += 97;
			array.add(r);

			return o;
		};

		int o = fn.apply(ascii, result);
		while (o > 0) {
			o = fn.apply(o, result);
		}
		return result;
	}

	private static String translateAsciiToString(int ascii) {
		List<Integer> array = make_ascii_array(ascii);

		StringBuilder result = new StringBuilder();
		for (int asc : array) {
			result.append((char) asc);
		}
		return result.toString();
	}

	public static String makeCrudToken() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
		int day = Calendar.getInstance().get(Calendar.DATE);

		int date = year * month * day;
		int alvin = translateStringtoAscrii("alvin") * month;
		int xenoimpact = translateStringtoAscrii("xenoimpact") * day;

		StringBuilder token = new StringBuilder();
		for (int asc : new int[]{date, alvin, xenoimpact}) {
			token.append(translateAsciiToString(asc));
		}

		return token.toString();
	}
}