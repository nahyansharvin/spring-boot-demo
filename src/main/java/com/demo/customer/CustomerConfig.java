package com.demo.customer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class CustomerConfig {

    @Bean
    CommandLineRunner commandLineRunner(CustomerRepository repository){
        return args -> {
            Customer nahyanSharvin = new Customer(
                    "Nahyan Sharvin",
                    "nahyan@gmail.com",
                    LocalDate.of(2002, 4, 28)
            );

            Customer soman = new Customer(
                    "Soman",
                    "soman@gmail.com",
                    LocalDate.of(2000, 1, 19)
            );

            repository.saveAll(
                    List.of(nahyanSharvin, soman)
            );
        };
    }
}
