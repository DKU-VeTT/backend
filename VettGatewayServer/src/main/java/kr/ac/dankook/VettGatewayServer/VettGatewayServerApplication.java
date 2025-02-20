package kr.ac.dankook.VettGatewayServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VettGatewayServerApplication {

	public static void main(String[] args) {

		SpringApplication.run(VettGatewayServerApplication.class, args);
	}

}
