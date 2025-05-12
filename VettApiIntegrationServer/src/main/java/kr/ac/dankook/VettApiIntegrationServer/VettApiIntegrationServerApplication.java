package kr.ac.dankook.VettApiIntegrationServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VettApiIntegrationServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettApiIntegrationServerApplication.class, args);
	}
}
