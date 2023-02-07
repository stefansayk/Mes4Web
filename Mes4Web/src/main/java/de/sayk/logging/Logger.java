package de.sayk.logging;

import org.apache.log4j.PropertyConfigurator;

public class Logger {

	private org.apache.log4j.Logger log;
	private static boolean isFirstRun = true;

	public static Logger getLogger(String className) {

		if (isFirstRun) {
			PropertyConfigurator.configure("log4j.properties");
			isFirstRun = false;
		}

		return new Logger(org.apache.log4j.Logger.getLogger(className));
	}

	public Logger(org.apache.log4j.Logger log) {
		this.log = log;
	}

	public void debug(String logText) {
		log.debug(logText);
	}

	public void debug(Object logObj) {
		log.debug(logObj.toString());
	}

	public void info(String logText) {
		log.info(logText);
	}

	public void error(String logText) {
		log.error(logText);
	}

	public void error(String logText, Exception e) {
		log.error(logText , e);
	}

	public void error(Exception e) {
		log.error(e);
	}

}
