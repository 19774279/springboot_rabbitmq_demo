package com.zsj.demo4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.MessageProperties;

/**
 * Created by zhusj on 2017/3/28.
 */
public class Send {
	final static String queueName="queuetwo";
	public static void  main(String args[]) throws Exception{
		Channel channel = GetChannel.getCh();
		channel.queueDeclare(queueName, true,false,false,null);
		channel.basicPublish("",queueName, MessageProperties.PERSISTENT_TEXT_PLAIN,"fuck".getBytes());
		GetChannel.close();
	}
}
