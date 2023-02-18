package cz.cvut.fit.wikimetric.pocclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PocClientApplication {

	public static void main(String[] args) {
		var app = new SpringApplication(PocClientApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run();
		//SpringApplication.run(PocClientApplication.class, args);
	}

}
