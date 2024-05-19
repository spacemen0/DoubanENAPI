package com.soma.doubanen;

import com.soma.doubanen.utils.index.Indexer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DoubanEnApplication {

  public static void main(String[] args) {
    SpringApplication.run(DoubanEnApplication.class, args);
  }

  @Bean
  public ApplicationRunner buildIndex(Indexer indexer) {
    return (ApplicationArguments args) ->
        indexer.indexPersistedData("com.soma.doubanen.domains.entities.MediaEntity");
  }
}
