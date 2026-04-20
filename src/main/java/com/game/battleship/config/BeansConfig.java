package com.game.battleship.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
public class BeansConfig {
    @Bean
    public Random numbersGenerator() {
     return new SecureRandom();
    }
}
