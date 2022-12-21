package vip.ings.mybatisplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("ings.vip.mybatisplus.*")
@SpringBootApplication
public class IngsApplication {

    public static void main(String[] args) {
        SpringApplication.run(IngsApplication.class, args);
    }

}
