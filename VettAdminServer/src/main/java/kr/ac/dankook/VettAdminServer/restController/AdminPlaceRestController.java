package kr.ac.dankook.VettAdminServer.restController;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/admin/place")
@RequiredArgsConstructor
public class AdminPlaceRestController {

    private final RestClient restClient;

    @Value("${admin.header.name}")
    private String ADMIN_HEADER_NAME;
//    @Value("${admin.gateway.server-url}")
//    private String ADMIN_GATEWAY_SERVER_URL;
}
