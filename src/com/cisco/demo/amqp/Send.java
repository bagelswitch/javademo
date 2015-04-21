package com.cisco.demo.amqp;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {
	  
	  public static void send(String queueName, String host, int port, String username, String password, String virtualHost, String message) throws java.io.IOException {
		  ConnectionFactory factory = new ConnectionFactory();
		  factory.setHost(host);
		  factory.setPort(port);
		  factory.setUsername(username);
		  factory.setPassword(password);
		  factory.setVirtualHost(virtualHost);
		  Connection connection = factory.newConnection();
		  Channel channel = connection.createChannel();
		  
		  channel.queueDelete(queueName);
		  
		  Map<String, Object> args = new HashMap<String, Object>();
		  args.put("x-expires", 1000);
		  channel.queueDeclare(queueName, false, false, false, args);
		  
		  channel.basicPublish("", "", null, message.getBytes());
		  System.out.println(" [x] Sent '" + message + "'");
		  
		  channel.close();
		  connection.close();
	  }
}
