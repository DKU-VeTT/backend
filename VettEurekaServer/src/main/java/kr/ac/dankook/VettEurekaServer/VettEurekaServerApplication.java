package kr.ac.dankook.VettEurekaServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class VettEurekaServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(VettEurekaServerApplication.class, args);
	}
}
