package com.shop.ecommerce.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.topicexchange.shop}")
  private String exchange;

  @Value("${rabbitmq.queue.ml}")
  private String mlQueue;

  @Value("${rabbitmq.routingkey.ml}")
  private String mlRKey;

  @Value("${rabbitmq.queue.elastic}")
  private String elasticQueue;

  @Value("${rabbitmq.routingkey.elastic}")
  private String elasticRKey;

  // bean for rabbitmq exchange, elastic+ML
  @Bean
  public TopicExchange exchange(){
    return new TopicExchange(exchange);
  }

  // bean for rabbitmq ML queue
  @Bean
  public Queue mlQueue(){
    return new Queue(mlQueue);
  }

  // bean for rabbitmq elastic queue
  @Bean
  public Queue elasticQueue(){
    return new Queue(elasticQueue);
  }

  // bean between queue and exchange for ml
  @Bean
  public Binding mlBinding(){
    return BindingBuilder.bind(mlQueue())
            .to(exchange())
            .with(mlRKey);
  }

  // bean between queue and exchange for elastic
  @Bean
  public Binding elasticBinding(){
    return BindingBuilder.bind(elasticQueue())
            .to(exchange())
            .with(elasticRKey);
  }

  @Bean
  public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  // Connection factory, Rabbit template, Rabbit admin will be configured by spring.

}
