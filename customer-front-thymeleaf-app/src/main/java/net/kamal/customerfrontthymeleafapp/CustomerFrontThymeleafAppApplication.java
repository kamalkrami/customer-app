package net.kamal.customerfrontthymeleafapp;

import net.kamal.customerfrontthymeleafapp.entites.Customer;
import net.kamal.customerfrontthymeleafapp.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class CustomerFrontThymeleafAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerFrontThymeleafAppApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(CustomerRepository customerRepository) {
        return args -> {
            List<Customer> customerList = List.of(Customer.builder().name("kamal").email("kamal@gmail.com").build(),
                    Customer.builder().name("krami").email("krami@gmail.com").build()
            );
            customerRepository.saveAll(customerList);
        };
    }
}
