package kr.ac.dankook.VettAuthServer.service;

import io.grpc.stub.StreamObserver;
import kr.ac.dankook.MemberSync;
import kr.ac.dankook.MemberSyncServiceGrpc;
import kr.ac.dankook.VettAuthServer.entity.Member;
import kr.ac.dankook.VettAuthServer.repository.MemberRepository;
import kr.ac.dankook.VettAuthServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberGrpcService extends MemberSyncServiceGrpc.MemberSyncServiceImplBase {

    private final MemberRepository memberRepository;

    public void sendMemberInfo(MemberSync.MemberSyncRequest request, StreamObserver<MemberSync.MemberSyncResponse> responseObserver) {
        log.info("Received MemberSyncRequest with ID: {}", request.getId());
        Optional<Member> member = memberRepository.findById(EncryptionUtil.decrypt(request.getId()));
        MemberSync.MemberSyncResponse response = null;
        if (member.isPresent()) {
            Member memberInfo = member.get();
            response = MemberSync.MemberSyncResponse.newBuilder()
                    .setId(request.getId())
                    .setUserId(memberInfo.getUserId())
                    .setEmail(memberInfo.getEmail())
                    .setName(memberInfo.getName())
                    .build();
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
