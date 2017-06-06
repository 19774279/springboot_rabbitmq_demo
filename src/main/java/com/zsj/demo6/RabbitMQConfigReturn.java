package com.zsj.demo6;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Created by zhusj on 2017/3/28.
 */
@Component
public class RabbitMQConfigReturn {
	public static final String EXCHANGE = "my-mq-exchange";
	public static final String ROUTINGKEY1 = "queue_one_key1";

	/**
	 * 初始化RabbitMQ连接
	 * @return
	 */
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("172.16.1.249", 5672);

		connectionFactory.setUsername("dep_user");
		connectionFactory.setPassword("dep_user");
		connectionFactory.setVirtualHost("dep");

		return connectionFactory;
	}

	/**
	 * 新建exchange
	 * @return
	 */
	@Bean
	public DirectExchange defaultExchange() {
		return new DirectExchange(EXCHANGE, true, false);
	}

	/**
	 * 新建队列1
	 * @return
	 */
	@Bean
	public Queue queue() {
		return new Queue("queue_one", true);
	}

	@Bean
	public Queue replyQueue() {
		return new Queue("queueReply", true);
	}

	/**
	 * 绑定队列1和exchange
	 * @return
	 */
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue()).to(defaultExchange()).with(RabbitMQConfigReturn.ROUTINGKEY1);
	}

	/**
	 * 接收队列1的消息
	 * @return
	 */
	@Bean
	public SimpleMessageListenerContainer messageContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(queue());
		container.setExposeListenerChannel(true);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				System.out.println("收到消息1：" + new String(body));
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
		});
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer replyListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory());
		container.setQueues(replyQueue());
//		container.setMessageListener(fixedReplyQRabbitTemplate());
		return container;
	}
}
