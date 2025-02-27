package kr.ac.dankook.VettAuthServer.admin.adminDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminPasswordChangeRequest {

    private Long adminId;
    private String adminNewPassword;
}
