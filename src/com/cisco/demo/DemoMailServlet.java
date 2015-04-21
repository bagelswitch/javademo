package com.cisco.demo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DemoServlet
 */
@WebServlet("/DemoMailServlet")
public class DemoMailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DemoMailServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    static {

    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		
		String result = "";
		
		String toAddr = request.getParameter("to");
		String fromAddr = request.getParameter("from");
		String subject = request.getParameter("subject");
		String body = request.getParameter("body");
		
		String host = System.getenv("mail_smtp_host");
		String port = System.getenv("mail_smtp_port");
		String domain = System.getenv("mail_smtp_localhost");
		//String user = "mailuser";
		//String password = "password";
	    
	    Properties props = System.getProperties();
	    props.put("mail.smtp.starttls.enable", "false");
	    props.put("mail.smtp.host", host);
	    props.put("mail.smtp.port", port);
	    //props.put("mail.smtp.user", user);
	    //props.put("mail.smtp.password", password);
	    props.put("mail.smtp.localhost", domain);
	    props.put("mail.smtp.auth", "false");
	    props.setProperty("mail.debug", "true");
	    
	    String[] to = {toAddr};

	    try {
		    Session session = Session.getInstance(props);
		    session.setDebug(true);
		    MimeMessage message = new MimeMessage(session);
		    message.setFrom(new InternetAddress(fromAddr));
	
		    InternetAddress[] toAddress = new InternetAddress[to.length];
	
		    for( int i=0; i < to.length; i++ ) { 
		        toAddress[i] = new InternetAddress(to[i]);
		    }
	
		    for( int i=0; i < toAddress.length; i++) { 
		        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
		    }
		    message.setSubject(subject);
		    message.setText(body);
		    Transport transport = session.getTransport("smtp");   
		    transport.connect(); //host, user, password);
		    transport.sendMessage(message, message.getAllRecipients());
		    transport.close();
		    
		    result = "Sent mail!";
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	result = "Exception: " + e.getMessage();
	    }
		
		out.write(result);
		out.flush();
		out.close();
	}
}
