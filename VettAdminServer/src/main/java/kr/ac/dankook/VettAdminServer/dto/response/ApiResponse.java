package kr.ac.dankook.VettAdminServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {

    private final boolean success = true;
    private int statusCode;
    private T Data;
}
