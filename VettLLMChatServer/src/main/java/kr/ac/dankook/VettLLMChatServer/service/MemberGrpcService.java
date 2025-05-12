package kr.ac.dankook.VettLLMChatServer.service;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import kr.ac.dankook.MemberSync;
import kr.ac.dankook.MemberSyncServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MemberGrpcService {

    private final MemberSyncServiceGrpc.MemberSyncServiceBlockingStub stub;

    public MemberGrpcService(
            @Value("${spring.grpc.server.port}") int port,
            @Value("${spring.grpc.server.host}") String grpcHost) {

        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(grpcHost, port)
                .usePlaintext()
                .build();
        stub = MemberSyncServiceGrpc.newBlockingStub(channel);
    }

    public MemberSync.MemberSyncResponse sendMemberTraceInfo(String memberId) {
        MemberSync.MemberSyncRequest request = MemberSync.MemberSyncRequest.newBuilder()
                .setId(memberId)
                .build();
        return stub.sendMemberInfo(request);
    }
}
