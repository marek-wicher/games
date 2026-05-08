package com.game.battleship.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.CountedMeterTagAnnotationHandler;
import io.micrometer.core.aop.MeterTagAnnotationHandler;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
@EnableAspectJAutoProxy
public class BeansConfig {
    @Bean
    public Random numbersGenerator() {
        return new SecureRandom();
    }

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
    @Bean
    MeterTagAnnotationHandler meterTagAnnotationHandler() {
        return new MeterTagAnnotationHandler(
                aClass -> Object::toString,
                aClass -> (exp, param) -> param.toString()
        );
    }

    @Bean
    public CountedAspect countedAspect() {
        CountedAspect aspect = new CountedAspect();
        CountedMeterTagAnnotationHandler tagAnnotationHandler = new CountedMeterTagAnnotationHandler(
                aClass -> Object::toString,
                aClass -> (exp, param) -> "");
        aspect.setMeterTagAnnotationHandler(tagAnnotationHandler);
        return aspect;
    }

//    @Bean
//    public MeterRegistry meterRegistry() {
//        return new CompositeMeterRegistry();
//    }
}
