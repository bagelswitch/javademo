package com.cisco.demo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cassandra.thrift.KsDef;

import com.cisco.demo.amqp.Receiver;
import com.cisco.demo.amqp.Send;
import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.AuthenticationCredentials;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.SimpleAuthenticationCredentials;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

/**
 * Servlet implementation class DemoServlet
 */
@WebServlet("/DemoServlet")
public class DemoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static final Keyspace keyspace = null;
	
	private static String CASSANDRA_HOST_LIST;
	private static String CASSANDRA_PORT;
	private static String CASSANDRA_USERNAME;
	private static String CASSANDRA_PASSWORD;
	
	private static String RABBIT_HOSTNAME = "127.0.0.1";
	private static String RABBIT_PORT = "5672";
	private static String RABBIT_USERNAME ="conversation";
	private static String RABBIT_PASSWORD = "changeme";
	private static String RABBIT_VIRTUALHOST = "/";
	private static String RABBIT_QUEUENAME = "integration.conversation.queue";
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DemoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    static {
    	try {
	    	// obtain CF environment
	    	//final String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
	    	//System.out.println(VCAP_SERVICES);
	    	
	    	// obtain Cassandra connection details
	    	/*
	    	JsonReader reader = Json.createReader(new ByteArrayInputStream(VCAP_SERVICES.getBytes("UTF-8")));
	    	JsonObject json = reader.readObject();
	    	json = (JsonObject) json.getJsonArray("user-provided").get(1);
	    	json = (JsonObject) json.getJsonObject("credentials");
	    	CASSANDRA_HOST_LIST = json.getString("hostAddressList");
	    	CASSANDRA_PORT = json.getString("port");
	    	CASSANDRA_USERNAME = json.getString("username");
	    	CASSANDRA_PASSWORD = json.getString("password");
	    	
	    	CASSANDRA_HOST_LIST = CASSANDRA_HOST_LIST.replace(",", ":" + CASSANDRA_PORT + ",") + ":" + CASSANDRA_PORT;
	    	System.out.println("Got Cassandra credentials: " + CASSANDRA_USERNAME + ":" + CASSANDRA_PASSWORD + "@" + CASSANDRA_HOST_LIST);
	    	*/
	    	//obtain RabbitMQ connection details
	    	/*  
	    	JsonReader reader = Json.createReader(new ByteArrayInputStream(VCAP_SERVICES.getBytes("UTF-8")));
	    	JsonObject json = reader.readObject();
	    	json = (JsonObject) json.getJsonArray("user-provided").get(2);
	    	json = (JsonObject) json.getJsonObject("credentials");
	    	RABBIT_HOSTNAME = json.getString("hostname");
	    	RABBIT_PORT = json.getString("port");
	    	*/
	    	
	    	//System.out.println("Got Rabbit connection info: " + RABBIT_HOSTNAME + ":" + RABBIT_PORT);
	    	
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	/*
    	AuthenticationCredentials creds = new SimpleAuthenticationCredentials(CASSANDRA_USERNAME, CASSANDRA_PASSWORD);
    	AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder()
        .forCluster("cf2test")
        .forKeyspace("demo")
        .withAstyanaxConfiguration(new AstyanaxConfigurationImpl()
        	.setCqlVersion("3.0.0")
        	.setTargetCassandraVersion("1.2")
            .setDiscoveryType(NodeDiscoveryType.NONE)
        )
        .withConnectionPoolConfiguration(new ConnectionPoolConfigurationImpl("cf2ConnectionPool")
            .setPort(Integer.parseInt(CASSANDRA_PORT))
            .setMaxConnsPerHost(1)
            .setSeeds(CASSANDRA_HOST_LIST)
            .setAuthenticationCredentials(creds)
        )
        .withConnectionPoolMonitor(new DemoConnectionPoolMonitorImpl())
        .buildKeyspace(ThriftFamilyFactory.getInstance());

    	context.start();
    	keyspace = context.getClient();
    	*/
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
		
		RABBIT_QUEUENAME = request.getParameter("queue");
		
		String time = SimpleDateFormat.getDateTimeInstance().format(new Date());
		
		JsonObjectBuilder builder = Json.createObjectBuilder();
		builder.add("serviceName", "javademo");
		builder.add("serviceState", "online");
		builder.add("lastUpdated", time);
		
		JsonArrayBuilder serviceBuilder = Json.createArrayBuilder();
		/*
		// add cassandra service info
		JsonObjectBuilder cassBuilder = Json.createObjectBuilder();
		cassBuilder.add("serviceName", "cassandra");
		cassBuilder.add("serviceState", checkCassandra()?"online":"fault");
		cassBuilder.add("lastUpdated", time);
		cassBuilder.add("upstreamServices", Json.createArrayBuilder().build());
		serviceBuilder.add(cassBuilder.build());
		*/
		// add rabbit service info
		/*JsonObjectBuilder rabbitBuilder = Json.createObjectBuilder();
		rabbitBuilder.add("serviceName", "rabbitmq");
		rabbitBuilder.add("serviceState", checkRabbitMQ()?"online":"fault");
		rabbitBuilder.add("lastUpdated", time);
		rabbitBuilder.add("upstreamServices", Json.createArrayBuilder().build());
		serviceBuilder.add(rabbitBuilder.build());
		*/
		builder.add("upstreamServices", serviceBuilder.build());
		
		String result = builder.build().toString();
		
		response.setContentType("application/json");
		
		//response.addHeader("Connection", "Keep-Alive");
		//response.addHeader("Keep-Alive", "timeout=20");
		
		out.write(result);
		out.flush();
		out.close();
	}
	
	private boolean checkCassandra() {
		String result = "";
		try {
			result = keyspace.getKeyspaceName() + " cf_defs";
			result += ": " + keyspace.describeKeyspace().getFieldValue(KsDef._Fields.CF_DEFS.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result.contains("keyspace:demo, name:test")) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean checkRabbitMQ() {
		String message = "testing";
		String result = "";
		try {
			new Receiver(RABBIT_QUEUENAME, RABBIT_HOSTNAME, Integer.parseInt(RABBIT_PORT), RABBIT_USERNAME, RABBIT_PASSWORD, RABBIT_VIRTUALHOST).start();
			Send.send(RABBIT_QUEUENAME, RABBIT_HOSTNAME, Integer.parseInt(RABBIT_PORT), RABBIT_USERNAME, RABBIT_PASSWORD, RABBIT_VIRTUALHOST, message);
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return message.equals(Receiver.lastResult);
	}
}
