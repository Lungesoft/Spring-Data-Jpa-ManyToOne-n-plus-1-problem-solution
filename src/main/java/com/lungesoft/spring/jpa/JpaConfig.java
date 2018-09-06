package com.lungesoft.spring.jpa;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(repositoryBaseClass = EntityGraphJpaRepository.class)
public class JpaConfig {
}
