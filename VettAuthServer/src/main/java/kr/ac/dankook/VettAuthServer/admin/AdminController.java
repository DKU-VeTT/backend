package kr.ac.dankook.VettAuthServer.admin;

import kr.ac.dankook.VettAuthServer.admin.adminDto.AdminPasswordChangeRequest;
import kr.ac.dankook.VettAuthServer.dto.response.ApiResponse;
import kr.ac.dankook.VettAuthServer.entity.Member;
import kr.ac.dankook.VettAuthServer.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminController {

    private final MemberService memberService;

    @GetMapping("/members")
    public ResponseEntity<ApiResponse<List<Member>>> findAllMembersByAdmin(){
        return ResponseEntity.ok(new ApiResponse<>(200,new ArrayList<>(memberService.findAllMemberProcess())));
    }
    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Boolean>> changeAdminPassword(
            @RequestBody AdminPasswordChangeRequest adminPasswordChangeRequest
    ) {
        memberService.editMemberPasswordProcess(adminPasswordChangeRequest.getAdminId(),
                adminPasswordChangeRequest.getAdminNewPassword());
        return ResponseEntity.ok(new ApiResponse<>(200,true));
    }
}
