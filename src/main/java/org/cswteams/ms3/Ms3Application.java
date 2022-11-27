package org.cswteams.ms3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
public class Ms3Application {

	public static void main(String[] args) {
		SpringApplication.run(Ms3Application.class, args);
	}

}
