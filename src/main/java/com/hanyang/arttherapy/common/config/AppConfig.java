package com.hanyang.arttherapy.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hanyang.arttherapy.common.init.DirectoryInit;

@Configuration
public class AppConfig {

  @Bean
  public DirectoryInit directoryInit() {
    return new DirectoryInit();
  }
}
