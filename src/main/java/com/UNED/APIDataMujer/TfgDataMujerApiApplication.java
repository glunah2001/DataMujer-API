package com.UNED.APIDataMujer;

import com.UNED.APIDataMujer.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TfgDataMujerApiApplication {
	public static void main(String[] args) {
		SpringApplication.run(TfgDataMujerApiApplication.class, args);
	}
}
