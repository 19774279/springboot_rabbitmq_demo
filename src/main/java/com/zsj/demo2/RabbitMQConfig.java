package com.zsj.demo2;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 项目参考地址：http://www.raye.wang/2016/12/08/spring-bootji-cheng-rabbitmq/<br>
 * 本类与SendController是同一个项目，测试地址可以是：http://localhost:8080/send1?msg=aaaa和http://localhost:8080/send2?msg=bbbb<br>
 * Created by zhusj on 2017/3/27.
 */
@Component
public class RabbitMQConfig {
	public static final String EXCHANGE = "my-mq-exchange";
	public static final String ROUTINGKEY1 = "queue_one_key1";
	public static final String ROUTINGKEY2 = "queue_one_key2";

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
		connectionFactory.setPublisherConfirms(true);
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

	/**
	 * 绑定队列1和exchange
	 * @return
	 */
	@Bean
	public Binding binding() {
		return BindingBuilder.bind(queue()).to(defaultExchange()).with(RabbitMQConfig.ROUTINGKEY1);
	}

	/**
	 * 新建队列2
	 * @return
	 */
	@Bean
	public Queue queue1() {
		return new Queue("queue_one1", true);
	}

	/**
	 * 绑定队列2和exchange
	 * @return
	 */
	@Bean
	public Binding binding1() {
		return BindingBuilder.bind(queue1()).to(defaultExchange()).with(RabbitMQConfig.ROUTINGKEY2);
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
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				System.out.println("收到消息：" + new String(body));
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
		});
		return container;
	}

	/**
	 * 接收队列2的消息
	 * @return
	 */
	@Bean
	public SimpleMessageListenerContainer messageContainer2() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory());
		container.setQueues(queue1());
		container.setExposeListenerChannel(true);
		container.setMaxConcurrentConsumers(1);
		container.setConcurrentConsumers(1);
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setMessageListener(new ChannelAwareMessageListener() {
			@Override
			public void onMessage(Message message, Channel channel) throws Exception {
				byte[] body = message.getBody();
				System.out.println("queue1收到消息： " + new String(body));
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			}
		});
		return container;
	}
}
