package com.project.salebe.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ModelMapper defaultMapper() {
        ModelMapper m = new ModelMapper();
        m.getConfiguration()
                .setCollectionsMergeEnabled(false)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return m;
    }
}
