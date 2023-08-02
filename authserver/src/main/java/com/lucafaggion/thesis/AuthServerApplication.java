package com.lucafaggion.thesis;

import java.io.FileWriter;
import java.io.IOException;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// @SpringBootApplication(scanBasePackages = "com.lucafaggion.thesis")
// @EnableJpaRepositories(basePackages = "com.lucafaggion.thesis")
// @EntityScan(basePackages = "com.lucafaggion.thesis")
@SpringBootApplication
public class AuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthServerApplication.class, args);
	}

  //
  // https://mkyong.com/spring-boot/how-to-display-all-beans-loaded-by-spring-boot/#:~:text=In%20Spring%20Boot%2C%20you%20can,loaded%20by%20the%20Spring%20container.
  @Bean
  public CommandLineRunner run(ApplicationContext appContext) throws IOException {
    return args -> {

      FileWriter beanFile = new FileWriter("src/main/resources/beans.txt");
      String[] beans = appContext.getBeanDefinitionNames();
      for (String bean : beans) {
        beanFile.write(bean + " of Type :: " + appContext.getBean(bean).getClass() +
            "\n");
      }
      beanFile.close();
    };
  }

}
