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
	
	public static String getSampleString1(String ori){
		StringBuffer sb = new StringBuffer();
		
		for(int i=0; i<ori.length()/4;i++){
			int sum=0;
			for(int j=0; j<4;j++){
				char c = ori.charAt(4*i+j);
				int ci = Integer.parseInt(""+c);
				sum+=ci;
			}
			sb.append(sum); 
		}
		System.out.println(sb.toString());
		return sb.toString();
	}
	
	public static String getSampleString(String ori){
		StringBuffer sb = new StringBuffer();
		
		int sampleRate=2;
		for(int i=0;i<ori.length()/sampleRate; i++){
			sb.append(ori.charAt(i*sampleRate));
		}
		return sb.toString();
	}
	
	
	public static void showD(int[][] d){
		for(int i=0; i<d.length;i++){
			int dd[] = d[i];
			for(int j=0; j<dd.length;j++){
				System.out.print(dd[j]);
			}
			System.out.println("*");
		}
		System.out.println();
		System.out.println("---------------------------");
	}
	
	
public static int LD3(String s, String t) {
		
//		s=getSampleString(s);
//		t=getSampleString(t);
		long start =System.currentTimeMillis();
		
		if (Math.abs(s.length() - t.length()) > THRESHOLD)
			return 200;
		int n = s.length();
		int m = t.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		
		int dis = m/3;
		int d[][] = new int[n + 1][dis + 1];
		for (int i = 0; i <= n; i++)
			d[i][0] = i;

		for (int j = 0; j <= dis; j++)
			d[0][j] = j;


//		int beg,end=0;
		for (int i = 1; i <= n; i++) {
			char s_i = s.charAt(i-1);
			showD(d);
//			beg=i-dis>=1?i-dis:1;
//			end=i+dis<=m?i+dis:m;
			for (int j = 1; j <= dis; j++) {
//			for (int j = beg; j <= end; j++) {
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

//		long end =System.currentTimeMillis();
//		System.out.println(" distance: "+d[n][m]+"  "+  (end-start)+" ms consumed. ");
		return d[n][dis];
	}
	

public static int LD2(String s, String t){
	if (Math.abs(s.length() - t.length()) > THRESHOLD)
		return 500;
	int size=100;
	int sl = s.length()/size;
	int tl = t.length()/size;
	int dis=0;
	String[] ss = new String[sl+1];
	String[] tt = new String[tl+1];
	
	for(int i=0;i<sl;i++){
		ss[i]=s.substring(i*size, (i+1)*size);
	}
	ss[sl]=s.substring(sl*size);
	for(int i=0;i<tl;i++){
		tt[i]=t.substring(i*size, (i+1)*size);
	}
	tt[tl]=t.substring(tl*size);
	for(int i=0; i<ss.length;i++){
		if(i<tt.length){
			dis+=LD2(ss[i],tt[i]);
		}
	}
	dis+=LD2(ss[sl],tt[tl]);
	
	
	return dis;
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
//				showD(d);
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
		long start1 =System.currentTimeMillis();
//		Field field[] = Data.class.getDeclaredFields();
//		for(Field f:field){
//			long start =System.currentTimeMillis();
//			int i = Distance.LD(Data.MODEL_BOLD_2, f.get(new Data()).toString());
//			
//			long end =System.currentTimeMillis();
//			System.out.println("Distance: "+i+"  "+f.getName()+" "+  (end-start)+" ms consumed. ");
//		}
		
		int i = Distance.LD("100011100101","1100010010101");
		System.out.println("dis: "+i);
		long end1 =System.currentTimeMillis();
		System.out.println("Total: "+"  "+  (end1-start1)+" ms consumed. ");
	}
}
