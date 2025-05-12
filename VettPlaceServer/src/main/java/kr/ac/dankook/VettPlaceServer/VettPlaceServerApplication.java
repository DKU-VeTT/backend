package kr.ac.dankook.VettPlaceServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VettPlaceServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettPlaceServerApplication.class, args);
	}

}
