package com.zsj.demo8;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhusj on 2017/3/29.
 */
@Configuration
@Controller
@EnableAutoConfiguration
public class RequestReplyFixed {

	@Autowired
	private ConnectionFactory rabbitConnectionFactory;

	@Autowired
	private RabbitTemplate fixedReplyQRabbitTemplate;

	@RequestMapping("/")
	@ResponseBody
	String home() throws Exception {
		String str = (String) this.fixedReplyQRabbitTemplate.convertSendAndReceive("Hello, world!");
		return str;
	}

	@Bean
	public RabbitTemplate fixedReplyQRabbitTemplate() {
		RabbitTemplate template = new RabbitTemplate(this.rabbitConnectionFactory);
		template.setExchange(ex().getName());
		template.setRoutingKey("test");
		template.setReplyQueue(replyQueue());
		return template;
	}

	@Bean
	public SimpleMessageListenerContainer replyListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(this.rabbitConnectionFactory);
		container.setQueues(replyQueue());
		container.setMessageListener(fixedReplyQRabbitTemplate());
		return container;
	}

	@Bean
	public SimpleMessageListenerContainer serviceListenerContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(this.rabbitConnectionFactory);
		container.setQueues(requestQueue());
		container.setMessageListener(new MessageListenerAdapter(new PojoListener()));
		return container;
	}

	@Bean
	public DirectExchange ex() {
		return new DirectExchange("ex");
	}

	@Bean
	public Binding binding() {
		return BindingBuilder.bind(requestQueue()).to(ex()).with("test");
	}

	@Bean
	public Queue requestQueue() {
		return new Queue("my.request.queue");
	}

	@Bean
	public Queue replyQueue() {
		return new Queue("my.reply.queue");
	}

	public static class PojoListener {
		public String handleMessage(String foo) {
			return foo.toUpperCase();
		}
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RequestReplyFixed.class, args);
	}

}
