package com.sentury.approvalflow.producer;

import com.sentury.approvalflow.config.RabbitConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author xbhog
 */
@Slf4j
@Component
public class NormalRabbitProducer {

    @Value("${flow.exchange_name}")
    public String EXCHANGE_NAME;
    @Value("${flow.queue_name}")
    public String QUEUE_NAME;
    @Value("${flow.routing_key}")
    public String ROUTING_KEY;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String message) {
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
        log.info("【生产者】Message send: " + message);
    }


}
