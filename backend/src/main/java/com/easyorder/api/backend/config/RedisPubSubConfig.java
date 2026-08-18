package com.easyorder.api.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import com.easyorder.api.backend.service.RedisSubscriberService;

@Configuration
public class RedisPubSubConfig {

  public static final String SSE_CHANNEL = "easyorder:sse-events";

  @Bean
  public RedisMessageListenerContainer redisContainer(
      RedisConnectionFactory connectionFactory,
      MessageListenerAdapter listenerAdapter) {

    RedisMessageListenerContainer container = new RedisMessageListenerContainer();

    container.setConnectionFactory(connectionFactory);

    container.addMessageListener(
        listenerAdapter,
        new ChannelTopic(SSE_CHANNEL));

    return container;
  }

  @Bean
  public MessageListenerAdapter listenerAdapter(
      RedisSubscriberService subscriber) {

    return new MessageListenerAdapter(
        subscriber,
        "handleMessage");
  }
}