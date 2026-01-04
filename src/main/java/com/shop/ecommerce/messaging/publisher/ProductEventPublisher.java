package com.shop.ecommerce.messaging.publisher;


import com.shop.ecommerce.messaging.dto.ProductEventDto;
import com.shop.ecommerce.messaging.dto.ProductEventType;
import com.shop.ecommerce.messaging.dto.ProductSyncDto;
import com.shop.ecommerce.model.Product;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProductEventPublisher {

  private final String exchange;
  private final RabbitTemplate rabbitTemplate;


  public ProductEventPublisher(RabbitTemplate rabbitTemplate,
                               @Value("${rabbitmq.topicexchange.shop}") String exchange) {

    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
  }

  public void publish(ProductEventType type, Product product) {
    String routingKey = switch (type) {
      case CREATED -> "product.created";
      case UPDATED -> "product.updated";
      case DELETED -> "product.deleted";
    };

    var payload = ProductSyncDto.fromProduct(product);
    var event = new ProductEventDto(type, payload);

    rabbitTemplate.convertAndSend(exchange, routingKey, event);
  }
}



//  public void saveProductInElastic(Product p) {
//    var evt = new ProductIndexedEvent(p.getId(),p.getCategory(),p.getProductName(),p.getPrice(),p.getTags(),p.getDescription());
//  }

