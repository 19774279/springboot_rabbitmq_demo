package com.zsj.demo4;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

/**
 * Created by zhusj on 2017/3/28.
 */
public class Recvier {
	final static String queueName="queuetwo";

	public static void main(String[] args)throws Exception {
		Channel channel = GetChannel.getCh();
		channel.queueDeclare(queueName, true, false, false, null);
		channel.basicQos(1);
		QueueingConsumer consumer=new QueueingConsumer(channel);
		channel.basicConsume(queueName,false,consumer);
		while (true){
			QueueingConsumer.Delivery delivery=consumer.nextDelivery();
			String message = new String(delivery.getBody());
			System.out.println(message);
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(),true);
		}
	}
}
