package com.easyorder.api.backend;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect",
    "management.health.redis.enabled=false",
    "management.health.defaults.enabled=false",
    "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
    "jwt.secret=9a6182bd793546239f6772341b1e907673f13f282d1ae623bc4f480e306ec445",
    "jwt.expiration=86400000",
    "jwt.refresh-token.expiration=604800000"
})
class BackendApplicationTests {

    @MockitoBean
    private DataSource dataSource;

    @MockitoBean
    private Flyway flyway;

    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockitoBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockitoBean
    private org.springframework.data.redis.core.StringRedisTemplate stringRedisTemplate;

    @MockitoBean
    private RedisMessageListenerContainer redisMessageListenerContainer;

    @Test
    void contextLoads() {
    }

}
