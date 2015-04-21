package com.cisco.demo.amqp;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class Receiver extends Thread {
	  
	  private String host;
	  private int port;
	  private String username;
	  private String password;
	  private String virtualHost;
	  private String queueName;
	  
	  public static String lastResult = "";
	  
	  public Receiver (String queueName, String host, int port, String username, String password, String virtualHost) {
		  this.host = host;
		  this.port = port;
		  this.username = username;
		  this.password = password;
		  this.queueName = queueName;
	  }

	  public void run() {
		  try {
			  ConnectionFactory factory = new ConnectionFactory();
			  factory.setHost(this.host);
			  factory.setPort(this.port);
			  factory.setUsername(this.username);
			  factory.setPassword(this.password);
			  factory.setVirtualHost(this.virtualHost);
			  Connection connection = factory.newConnection();
			  Channel channel = connection.createChannel();
			  
			  channel.queueDelete(queueName);
			  
			  Map<String, Object> args = new HashMap<String, Object>();
			  args.put("x-expires", 1000);
			  channel.queueDeclare(queueName, false, false, false, args);
	
			  QueueingConsumer consumer = new QueueingConsumer(channel);
			  channel.basicConsume(queueName, true, consumer);
	
			  QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			  String message = new String(delivery.getBody());
			  System.out.println(" [x] Received '" + message + "'");
			  
			  lastResult = message;
		  } catch (Exception e) {
			  e.printStackTrace();
		  }
	  }
}