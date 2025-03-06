package kr.ac.dankook.VettAuthServer.controller;

import jakarta.validation.Valid;
import kr.ac.dankook.VettAuthServer.dto.request.*;
import kr.ac.dankook.VettAuthServer.dto.response.ApiResponse;
import kr.ac.dankook.VettAuthServer.dto.response.AuthMailResponse;
import kr.ac.dankook.VettAuthServer.dto.response.MailResponse;
import kr.ac.dankook.VettAuthServer.dto.response.TokenResponse;
import kr.ac.dankook.VettAuthServer.entity.Member;
import kr.ac.dankook.VettAuthServer.exception.ApiErrorCode;
import kr.ac.dankook.VettAuthServer.exception.ValidationException;
import kr.ac.dankook.VettAuthServer.service.AuthService;
import kr.ac.dankook.VettAuthServer.service.MailService;
import kr.ac.dankook.VettAuthServer.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth/identity")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final MemberService memberService;
    private final MailService mailService;

    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse<List<String>>> findId(@RequestBody @Valid FindIdRequest findIdRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(200, memberService.findUserIdProcess(findIdRequest)));
    }

    @PostMapping("/verify-code/{userId}")
    public ResponseEntity<ApiResponse<AuthMailResponse>> sendVerifyCode(@PathVariable String userId) throws IOException {
        if (authService.isExistUserIdProcess(userId)) {
            Member member = memberService.findMemberByUserIdProcess(userId);
            MailResponse mailResponse = mailService.createVerifyCodeMailResponse(member);
            mailService.sendMail(mailResponse);
            return ResponseEntity.ok(new ApiResponse<>(200,new AuthMailResponse(member.getEmail(),userId, true)));
        }
        return ResponseEntity.ok(new ApiResponse<>(200,new AuthMailResponse("USER_NOT_EXIT","USER_NOT_EXIT",false)));
    }

    @PostMapping("/is-verify")
    public ResponseEntity<ApiResponse<Boolean>> isVerifyCode(
            @RequestBody @Valid VerifyCodeRequest verifyCodeRequest,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        boolean isVerifyCode = mailService.isVerifyCodeProcess(
                verifyCodeRequest.getAuthMailToken(),
                verifyCodeRequest.getVerifyCode()
        );
        return ResponseEntity.ok(new ApiResponse<>(200,isVerifyCode));
    }

    @PatchMapping("/password")
    public ResponseEntity<ApiResponse<Boolean>> changeMemberPassword(
            @RequestBody @Valid PasswordChangeRequest passwordChangeRequest,
            BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        mailService.changePasswordByEmailProcess(
            passwordChangeRequest.getAuthMailToken(),
            passwordChangeRequest.getNewPassword()
        );
        return ResponseEntity.ok(new ApiResponse<>(200,true));
    }

    @PostMapping("/social")
    public ResponseEntity<ApiResponse<TokenResponse>> socialLogin(
            @RequestBody @Valid SocialSignInRequest socialSignInRequest, BindingResult bindingResult
    ) throws IOException {
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(201,authService.socialLoginProcess(socialSignInRequest)));
    }

    @GetMapping("/is-duplicate/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> isDuplicate(@PathVariable String userId){
        return ResponseEntity.ok(new ApiResponse<>(200,authService.isExistUserIdProcess(userId)));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@RequestBody @Valid SignupRequest signupRequest, BindingResult bindingResult) throws IOException {
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(201,authService.signupProcess(signupRequest)));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<ApiResponse<TokenResponse>> signIn(@RequestBody @Valid SignInRequest signInRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(200,authService.signInProcess(signInRequest)));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<TokenResponse>> reissue(
            @RequestBody @Valid TokenRequest tokenRequest, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            validateBindingResult(bindingResult);
        }
        return ResponseEntity.ok(new ApiResponse<>(200,authService.reissueTokenProcess(tokenRequest)));
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
