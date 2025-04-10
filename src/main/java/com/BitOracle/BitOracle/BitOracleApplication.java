package com.BitOracle.BitOracle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BitOracleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BitOracleApplication.class, args);
	}

}
