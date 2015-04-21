package com.cisco.demo.context;

import java.io.File;
import java.io.PrintWriter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DemoContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		try{
			System.out.println("***** Starting sleep before writing state file . . .");
			Thread.sleep(10000);
			new File("state_file.yaml").delete();
			System.out.println("***** Deleted old state file.");
	    	PrintWriter writer = new PrintWriter("state_file.yaml", "UTF-8");
	    	writer.println("---");
	    	writer.println("state: RUNNING");
	    	writer.close();
	    	System.out.println("***** Finished writing state file . . .");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}
}
