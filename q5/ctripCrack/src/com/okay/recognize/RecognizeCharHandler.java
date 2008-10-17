/**
 * 
 */
package com.okay.recognize;

import java.lang.reflect.Field;
import java.util.Map;

class RecognizeCharHandler implements Runnable {

	Data d = new Data();
	String simg;
	Field field;
	Map<Integer, String> result;

	RecognizeCharHandler(String simg, Field field,
			Map<Integer, String> result) {
		this.simg = simg;
		this.field = field;
		this.result = result;
	}

	public void run() {

		int distance = 0;
		try {
			distance = Distance.LD(simg, field.get(d).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String name = field.getName();
		result.put(new Integer(distance), name);
	}
}