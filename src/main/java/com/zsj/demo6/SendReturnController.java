package com.zsj.demo6;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by zhusj on 2017/3/28.
 */
@RestController
public class SendReturnController {

	private RabbitTemplate rabbitTemplate;

	/**
	 * 配置发送消息的rabbitTemplate，因为是构造方法，所以不用注解Spring也会自动注入（应该是新版本的特性）
	 *
	 * @param rabbitTemplate
	 */
	public SendReturnController(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
		//设置消费回调
		this.rabbitTemplate.setMandatory(true);
	}

	/**
	 * 向消息队列1中发送消息
	 *
	 * @param msg
	 * @return
	 */
	@RequestMapping("send1")
	public String send1(String msg) {
		String uuid = UUID.randomUUID().toString();
		CorrelationData correlationId = new CorrelationData(uuid);
//		rabbitTemplate.convertAndSend(RabbitMQConfigReturn.EXCHANGE, RabbitMQConfigReturn.ROUTINGKEY1, msg,
//				correlationId);
		Object o = rabbitTemplate.convertSendAndReceive(RabbitMQConfigReturn.EXCHANGE, RabbitMQConfigReturn.ROUTINGKEY1, msg,
				correlationId);
		return "ok";
	}
}