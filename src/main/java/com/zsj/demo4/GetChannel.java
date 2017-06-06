package com.zsj.demo4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by zhusj on 2017/3/28.
 */
public class GetChannel {
	private static Connection connection=null;
	private static Channel channel=null;
	public static Channel getCh() throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("172.16.1.249");
		factory.setPort(5672);

		factory.setUsername("dep_user");
		factory.setPassword("dep_user");
		factory.setVirtualHost("dep");
		connection = factory.newConnection();
		channel = connection.createChannel();
		return channel;
	}

	public static void close() throws IOException, TimeoutException {
		if(channel!=null){
			channel.close();
		}
		if(connection!=null){
			connection.close();
		}
	}
}
