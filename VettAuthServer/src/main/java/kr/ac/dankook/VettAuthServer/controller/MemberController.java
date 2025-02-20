package kr.ac.dankook.VettAuthServer.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettAuthServer.config.principal.PrincipalDetails;
import kr.ac.dankook.VettAuthServer.dto.request.EditMemberRequest;
import kr.ac.dankook.VettAuthServer.dto.request.EditPasswordRequest;
import kr.ac.dankook.VettAuthServer.dto.response.ApiMessageResponse;
import kr.ac.dankook.VettAuthServer.dto.response.ApiResponse;
import kr.ac.dankook.VettAuthServer.dto.response.MemberResponse;
import kr.ac.dankook.VettAuthServer.dto.response.TokenResponse;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.ValidationException;
import kr.ac.dankook.VettAuthServer.service.AuthService;
import kr.ac.dankook.VettAuthServer.service.MemberService;
import kr.ac.dankook.VettAuthServer.util.DecryptId;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/api/user")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMember(@PathVariable @DecryptId Long id) {
        MemberResponse apiResponse = memberService.convertMemberEntity(memberService.findMemberByIdProcess(id));
        return ResponseEntity.ok(
                new ApiResponse<>(200, apiResponse));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiMessageResponse> logoutMember(@AuthenticationPrincipal PrincipalDetails userDetails){
        if (authService.logoutProcess(userDetails)){
            return ResponseEntity.ok(new ApiMessageResponse(true,200,"Logout Success"));
        }else{
            return ResponseEntity.ok(new ApiMessageResponse(false,401,"Already Log Out"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessageResponse> deleteMember(@PathVariable @DecryptId Long id) {
        memberService.deleteMemberByIdProcess(id);
        return ResponseEntity.ok(new ApiMessageResponse(true,200,"Delete success member."));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<TokenResponse>> editMember(
            @PathVariable @DecryptId Long id,
            @RequestBody @Valid EditMemberRequest editMemberRequest,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        TokenResponse newToken = memberService.editMemberProcess(id,editMemberRequest);
        return ResponseEntity.ok(new ApiResponse<>(
                200,newToken));
    }

    @PatchMapping("/password/{id}")
    public ResponseEntity<ApiMessageResponse> editMemberPassword(
            @PathVariable @DecryptId Long id,
            @RequestBody @Valid EditPasswordRequest editPasswordRequest,
            BindingResult bindingResult
    ){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        memberService.editMemberPasswordProcess(id,editPasswordRequest.getPassword());
        return ResponseEntity.ok(new ApiMessageResponse(true,200,"Edit Password success."));
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
