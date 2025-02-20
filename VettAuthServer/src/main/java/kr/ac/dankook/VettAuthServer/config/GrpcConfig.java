package kr.ac.dankook.VettAuthServer.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import kr.ac.dankook.VettAuthServer.service.MemberGrpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class GrpcConfig {

    @Value("${spring.grpc.server.port}")
    private int port;

    @Bean
    public Server grpcServer(MemberGrpcService memberGrpcService) throws IOException {

        Server server = ServerBuilder
                .forPort(port)
                .addService(memberGrpcService)
                .build();

        server.start();
        log.info("gRPC Server started on port {}",port);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down gRPC server.");
            server.shutdown();
        }));

        return server;
    }
}
