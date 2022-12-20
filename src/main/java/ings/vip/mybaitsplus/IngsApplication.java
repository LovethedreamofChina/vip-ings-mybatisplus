package ings.vip.mybaitsplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("ings.vip.mybaitsplus.*")
@SpringBootApplication
public class IngsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngsApplication.class, args);
    }

}