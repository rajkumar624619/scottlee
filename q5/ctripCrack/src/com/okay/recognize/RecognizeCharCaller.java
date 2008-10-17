/**
 * 
 */
package com.okay.recognize;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;


class RecognizeCharCaller implements Callable<Result> {

	Data d = new Data();
	String simg;
	Field field;

	RecognizeCharCaller(String simg, Field field) {
		this.simg = simg;
		this.field = field;
	}

	public Result call() {
		Result r = new Result();
		int distance = 200;
		try {
			distance = Distance.LD(simg, field.get(d).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String name = field.getName();
		r.distance=distance;
		r.name=name;
		return r;
	}
}