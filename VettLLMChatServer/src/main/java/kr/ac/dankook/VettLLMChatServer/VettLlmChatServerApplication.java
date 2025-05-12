package kr.ac.dankook.VettLLMChatServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableDiscoveryClient
@EnableJpaAuditing
public class VettLlmChatServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(VettLlmChatServerApplication.class, args);
	}

}
