package com.lungesoft.spring.jpa;

import com.lungesoft.spring.jpa.entity.Address;
import com.lungesoft.spring.jpa.entity.City;
import com.lungesoft.spring.jpa.entity.Customer;
import com.lungesoft.spring.jpa.entity.Street;
import com.lungesoft.spring.jpa.repositoty.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Bean
    public CommandLineRunner demo(CustomerRepository repository) {
        return (args) -> {
            repository.save(new Customer("Jack", "Bauer", new Address(new City("city1"), new Street("street1"))));
            repository.save(new Customer("Jack", "O'Brian", new Address(new City("city2"), new Street("street2"))));
            repository.save(new Customer("Jack", "Bauer", new Address(new City("city3"), new Street("street3"))));
            repository.save(new Customer("Jack", "Palmer", new Address(new City("city4"), new Street("street4"))));
            repository.save(new Customer("Jack", "Dessler", new Address(new City("city5"), new Street("street5"))));
        };
    }

}
