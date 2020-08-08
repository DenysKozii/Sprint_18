package com.softserve.marathon;

import com.softserve.marathon.model.Role;
import com.softserve.marathon.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication/*(exclude = {SecurityAutoConfiguration.class})*/
public class  SoftServeMarathonApplication implements CommandLineRunner {
    @Autowired
    RoleRepository roleRepository;


    public static void main(String[] args) {
        SpringApplication.run(SoftServeMarathonApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.findByRole("STUDENT") == null) roleRepository.save(new Role("STUDENT"));
        if (roleRepository.findByRole("MENTOR") == null) roleRepository.save(new Role("MENTOR"));
        if (roleRepository.findByRole("ADMIN") == null) roleRepository.save(new Role("ADMIN"));
    }
}
