package kr.ac.dankook.VettAdminServer.dto.request;

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
