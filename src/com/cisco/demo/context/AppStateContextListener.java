package com.cisco.demo.context;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppStateContextListener implements ServletContextListener {
	
	private static final Logger LOG = LoggerFactory.getLogger(AppStateContextListener.class);
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// do nothing
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try {
			new File("state_file.yaml").delete();
			LOG.info("Deleted old state file.");
	    	PrintWriter writer;
			writer = new PrintWriter("state_file.yaml", "UTF-8");
	    	writer.println("---");
	    	writer.println("state: RUNNING");
	    	writer.close();
	    	LOG.info("Finished writing state file for application.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}