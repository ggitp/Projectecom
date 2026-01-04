package com.shop.ecommerce.messaging.publisher;


import com.shop.ecommerce.model.Product;
import com.shop.ecommerce.model.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MLEventPublisher {

  private final String exchange;
  private final String mlRKey;
  private final RabbitTemplate rabbitTemplate;

  public MLEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${rabbitmq.topicexchange.shop}") String exchange,
                               @Value("${rabbitmq.routingkey.ml}") String routingKey) {

    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.mlRKey = routingKey;
  }

}
