package com.ecomm.ecomm_cart_service_application.Repository;

import com.ecomm.ecomm_cart_service_application.dtos.CartDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class CartRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public CartRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public CartDto get(String key) {
        return (CartDto) redisTemplate.opsForValue().get(key);
    }

    public void save(String key, CartDto cartDto, Duration ttl) {
        redisTemplate.opsForValue().set(key, cartDto, ttl);
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

}
