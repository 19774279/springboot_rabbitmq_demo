package com.zsj.demo6;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

/**
 * Created by zhusj on 2017/3/29.
 */
//@Configurable
public class MqClientConfig {
	@Autowired
	ConnectionFactory connectionFactory;

	final static String REPLY_QUEUE_NAME = "replyQueue";

	@Bean
	public Queue responseQueue() {
		return new Queue(REPLY_QUEUE_NAME);
	}

	@Bean
	public RabbitTemplate amqpTemplate() {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setReplyQueue(responseQueue());
		return rabbitTemplate;
	}

	@Bean
	public SimpleMessageListenerContainer clientMessageListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueues(responseQueue());
		container.setMessageListener(amqpTemplate());

		return container;
	}
}
