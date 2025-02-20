package kr.ac.dankook.VettAuthServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success = true;
    private final int statusCode;
    private final T Data;
}
