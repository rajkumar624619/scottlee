package com.okay.recognize;

public class FastEditDistance {
	public static int LD(String str1, String str2) {
		if (str1 == null || str1.length() == 0) {
			if (str2 == null || str2.length() == 0) {
				return 0;
			} else {
				return str2.length();
			}
		}
		if (str2 == null || str2.length() == 0) {
			return str1.length();
		}
		int maxOffset = Math.max(str1.length(), str2.length());
		int c = 0;
		int offset1 = 0, offset2 = 0;
		int lcs = 0;
		while ((c + offset1 < str1.length()) && (c + offset2 < str2.length())) {
			if (str1.charAt(c + offset1) == str2.charAt(c + offset2)) {
				lcs++;
			} else {
				offset1 = offset2 = 0;
				for (int i = 0; i < maxOffset; i++) {
					if ((c + i < str1.length() && (str1.charAt(c + i) == str2.charAt(c)))) {
						offset1 = i;
						break;
					}
					if ((c + i < str2.length() && (str1.charAt(c) == str2.charAt(c + i)))) {
						offset2 = i;
						break;
					}
				}
			}
			c++;
		}
		return (int)((str1.length() + str2.length()) * 1.0 /2 - lcs);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "sdadakdjasldjsdj", t = "wodjwdodskedjslj";
		long then = System.currentTimeMillis();
		int result = Distance.LD(s, t);
		long now = System.currentTimeMillis();
		System.out.println(result);
		System.out.println(now - then);

	}

}
