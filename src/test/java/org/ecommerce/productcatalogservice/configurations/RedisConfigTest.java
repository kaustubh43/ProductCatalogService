package org.ecommerce.productcatalogservice.configurations;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RedisConfigTest {

    @Mock
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("getRedisTemplate creates RedisTemplate with connection factory set")
    void getRedisTemplate_createsTemplateWithConnectionFactory() {
        RedisConfig redisConfig = new RedisConfig();

        RedisTemplate<String, Object> template = redisConfig.getRedisTemplate(redisConnectionFactory);

        assertThat(template).isNotNull();
        assertThat(template.getConnectionFactory()).isEqualTo(redisConnectionFactory);
    }
}

