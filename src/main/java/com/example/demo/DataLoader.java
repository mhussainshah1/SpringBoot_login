package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    InvalidPasswordRepository invalidPasswordRepository;

    @Autowired
    UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {

        //Password
        invalidPasswordRepository.save(new InvalidPassword("azerty12!"));
        invalidPasswordRepository.save(new InvalidPassword("12345678!"));
        invalidPasswordRepository.save(new InvalidPassword("password123"));


        User user = new User("Muhammad",
                "Shah",
                "moe",
                "password123",
                "password123",
                "mhussainshah79@gmail.com",
                "mhussainshah79@gmail.com",
                true);
        userRepository.save(user);
    }
}
