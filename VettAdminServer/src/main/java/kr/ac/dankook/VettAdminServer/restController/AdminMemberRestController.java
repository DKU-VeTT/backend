package kr.ac.dankook.VettAdminServer.restController;

import jakarta.validation.Valid;
import kr.ac.dankook.VettAdminServer.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAdminServer.dto.request.AdminPasswordChangeRequest;
import kr.ac.dankook.VettAdminServer.dto.request.ChangePasswordRequest;
import kr.ac.dankook.VettAdminServer.dto.response.ApiResponse;
import kr.ac.dankook.VettAdminServer.entity.Member;
import kr.ac.dankook.VettAdminServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAdminServer.exception.ApiException;
import kr.ac.dankook.VettAdminServer.exception.ValidationException;
import kr.ac.dankook.VettAdminServer.service.MemberService;
import kr.ac.dankook.VettAdminServer.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/api")
@RequiredArgsConstructor
public class AdminMemberRestController {

    private final MemberService memberService;
    private final RestClient restClient;

    @Value("${admin.header.name}")
    private String ADMIN_HEADER_NAME;
    @Value("${admin.gateway.server-url}")
    private String ADMIN_GATEWAY_SERVER_URL;

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<Member>>> getAllMembers(){

        ResponseEntity<ApiResponse<List<Member>>> response = restClient.get()
                .uri(ADMIN_GATEWAY_SERVER_URL+"/admin/auth/members")
                .header(ADMIN_HEADER_NAME, EncryptionUtil.getEncryptedAdminKey())
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<List<Member>>>() {});

        return ResponseEntity.ok(response.getBody());
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Boolean>> changeAdminPassword(
            @AuthenticationPrincipal PrincipalDetails userDetail,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest,
            BindingResult bindingResult){

        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }

        Long adminPrimaryKey = userDetail.getMember().getId();
        if (!memberService.isValidAdminOldPassword(adminPrimaryKey, changePasswordRequest.getOldPassword())){
            throw new ApiException(ApiErrorCode.INVALID_OLD_PASSWORD);
        }

        AdminPasswordChangeRequest requestBody =
                new AdminPasswordChangeRequest(adminPrimaryKey,changePasswordRequest.getNewPassword());

        ResponseEntity<ApiResponse<Boolean>> response = restClient.patch()
                .uri(ADMIN_GATEWAY_SERVER_URL+"/admin/auth/change-password")
                .header(ADMIN_HEADER_NAME, EncryptionUtil.getEncryptedAdminKey())
                .body(requestBody)
                .retrieve()
                .toEntity(new ParameterizedTypeReference<ApiResponse<Boolean>>() {});
        return ResponseEntity.ok(response.getBody());
    }

    private void validateBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String errorMessages = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(","));
            throw new ValidationException(ApiErrorCode.INVALID_REQUEST,errorMessages);
        }
    }
}
