package kr.ac.dankook.VettAuthServer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
@EnableAsync
@RequiredArgsConstructor
public class VettAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettAuthServerApplication.class, args);
	}

}
