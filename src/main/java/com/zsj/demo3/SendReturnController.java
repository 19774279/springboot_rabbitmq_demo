package com.zsj.demo3;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by zhusj on 2017/3/28.
 */
@RestController
public class SendReturnController implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
//public class SendReturnController implements RabbitTemplate.ReturnCallback {
	private RabbitTemplate rabbitTemplate;
	/**
	 * 配置发送消息的rabbitTemplate，因为是构造方法，所以不用注解Spring也会自动注入（应该是新版本的特性）
	 * @param rabbitTemplate
	 */
	public SendReturnController(RabbitTemplate rabbitTemplate){
		this.rabbitTemplate = rabbitTemplate;
		//设置消费回调
		this.rabbitTemplate.setConfirmCallback(this);
		this.rabbitTemplate.setReturnCallback(this);
		this.rabbitTemplate.setMandatory(true);
	}

	/**
	 * 向消息队列1中发送消息
	 * @param msg
	 * @return
	 */
	@RequestMapping("send1")
	public String send1(String msg){
		String uuid = UUID.randomUUID().toString();
		CorrelationData correlationId = new CorrelationData(uuid);
		rabbitTemplate.convertAndSend(RabbitMQConfigReturn.EXCHANGE, RabbitMQConfigReturn.ROUTINGKEY1, msg,
				correlationId);
		return null;
	}

	/**
	 * 消息的回调，主要是实现RabbitTemplate.ConfirmCallback接口
	 * 注意，消息回调只能代表成功消息发送到RabbitMQ服务器，不能代表消息被成功处理和接受
	 */
	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		System.out.println("回调id:" + correlationData);
		if (ack) {
			System.out.println("消息已发送到RabbitMQ服务器");
		} else {
			System.out.println("消息未能发送到RabbitMQ服务器，失败原因:" + cause+"\n重新发送");
		}
		System.out.println("================");
		System.out.println("correlationData = " + correlationData);
		System.out.println("ack = " + ack);
		System.out.println("cause = " + cause);
		System.out.println("================");
	}

	@Override
	public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
		System.out.println("=======================");
		System.out.println("message = " + message);
		System.out.println("replyCode = " + replyCode);
		System.out.println("replyText = " + replyText);
		System.out.println("exchange = " + exchange);
		System.out.println("routingKey = " + routingKey);
		System.out.println("================");
	}
}