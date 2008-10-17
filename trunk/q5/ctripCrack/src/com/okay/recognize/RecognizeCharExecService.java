package com.okay.recognize;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RecognizeCharExecService {
	
	public static final ExecutorService es= Executors.newFixedThreadPool(50);
	
	public static Future<Result> getCharDis(String sImg, Field field){
		RecognizeCharCaller c = new RecognizeCharCaller(sImg, field);
		Future<Result> r= es.submit(c);
		return r;
	}
	
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		Field field[] = Data.class.getDeclaredFields();
		
		for(Field f:field){
			Future<Result> r = getCharDis(Data.MODEL_BOLD_3, f);
		}
		
		long end = System.currentTimeMillis();
		System.out.println((end-start)+" ms consumed. ");
	}
}
