package com.okay.validate;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class Debug extends ArrayList {
	Logger logger = Logger.getLogger(Debug.class);
	private static final String SPLIT_CHAR = " | ";

	public void addDebugTimeStamp(String debugInfo) {
		StringBuffer sb = new StringBuffer();

		long timestamp = System.currentTimeMillis();
		long lastTimeStamp = 0;
		if (!this.isEmpty()) {
			lastTimeStamp = ((DebugInfo) this.get(this.size() - 1))
					.getTimeStamp();
		}
		long interval = timestamp - lastTimeStamp;

		DebugInfo info = new DebugInfo();
		info.setTimeStamp(timestamp);
		info.setDebugInfo(debugInfo);
		info.setInterval(interval);
		this.add(info);

	}

	public static String getStamp() {
		DateFormat df = new SimpleDateFormat("HH:mm:ss:SS");
		String t = df.format(new Date());
		return t;

	}

	class DebugInfo {
		long timeStamp;
		String debugInfo;
		long interval;

		public long getTimeStamp() {
			return timeStamp;
		}

		public void setTimeStamp(long timeStamp) {
			this.timeStamp = timeStamp;
		}

		public String getDebugInfo() {
			return debugInfo;
		}

		public void setDebugInfo(String debugInfo) {
			this.debugInfo = debugInfo;
		}

		public long getInterval() {
			return interval;
		}

		public void setInterval(long interval) {
			this.interval = interval;
		}

		public String toString() {
			StringBuffer sb = new StringBuffer();
			sb.append(SPLIT_CHAR).append(getStamp()).append(SPLIT_CHAR).append(
					debugInfo).append(SPLIT_CHAR).append("interval: ").append(
					interval);

			return sb.toString();
		}
	}

}
