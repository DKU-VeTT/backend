package kr.ac.dankook.VettAdminServer.restController;

import kr.ac.dankook.VettAdminServer.dto.response.ApiResponse;
import kr.ac.dankook.VettAdminServer.dto.response.ChatRoomListResponse;
import kr.ac.dankook.VettAdminServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminChatRestController {

    private final RestClient restClient;

    @Value("${admin.header.name}")
    private String ADMIN_HEADER_NAME;
    @Value("${admin.gateway.server-url}")
    private String ADMIN_GATEWAY_SERVER_URL;

    @GetMapping("/chat/rooms")
    public ResponseEntity<ApiResponse<ChatRoomListResponse>> test(){

        ResponseEntity<ApiResponse<ChatRoomListResponse>> response = restClient.get()
                .uri(ADMIN_GATEWAY_SERVER_URL+"/admin/chat/rooms")
                .header(ADMIN_HEADER_NAME, EncryptionUtil.getEncryptedAdminKey())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<ChatRoomListResponse>>() {});

        return ResponseEntity.ok(response.getBody());
    }

}
