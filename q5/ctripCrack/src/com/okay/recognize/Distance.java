// Decompiled by Jad v1.5.8e2. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://kpdus.tripod.com/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi space 
// Source File Name:   Distance.java

package com.okay.recognize;

import java.lang.reflect.Field;

public class Distance {
	public static final int THRESHOLD = 150;

	public Distance() {
	}

	public static int Minimum(int a, int b, int c) {
		int mi = a;
		if (b < mi)
			mi = b;
		if (c < mi)
			mi = c;
		return mi;
	}

	public static String getSampleString(String ori){
		StringBuffer sb = new StringBuffer();
		
		int sampleRate=3;
		for(int i=0;i<ori.length()/sampleRate; i++){
			sb.append(ori.charAt(i*sampleRate));
		}
		return sb.toString();
	}
	
	public static int LD(String s, String t) {
		
		s=getSampleString(s);
		t=getSampleString(t);
		long start =System.currentTimeMillis();
		
		if (Math.abs(s.length() - t.length()) > THRESHOLD)
			return 200;
		int n = s.length();
		int m = t.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		int d[][] = new int[n + 1][m + 1];
		for (int i = 0; i <= n; i++)
			d[i][0] = i;

		for (int j = 0; j <= m; j++)
			d[0][j] = j;

		for (int i = 1; i <= n; i++) {
			char s_i = s.charAt(i - 1);
			for (int j = 1; j <= m; j++) {
				char t_j = t.charAt(j - 1);
				int cost;
				if (s_i == t_j)
					cost = 0;
				else
					cost = 1;
				d[i][j] = Minimum(d[i - 1][j] + 1, d[i][j - 1] + 1,
						d[i - 1][j - 1] + cost);
			}

		}

		long end =System.currentTimeMillis();
//		System.out.println(" distance: "+d[n][m]+"  "+  (end-start)+" ms consumed. ");
		return d[n][m];
	}

	public static void main(String args1[]) throws IllegalArgumentException, IllegalAccessException {
		
		Field field[] = Data.class.getDeclaredFields();
		for(Field f:field){
			long start =System.currentTimeMillis();
			int i = Distance.LD(Data.MODEL_BOLD_2, f.get(new Data()).toString());
			
			long end =System.currentTimeMillis();
			System.out.println("Distance: "+i+"  "+  (end-start)+" ms consumed. ");
		}
		
	}
}
