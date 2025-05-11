package kr.ac.dankook.VettChatServer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private final boolean success = true;
    private final int statusCode;
    private final T Data;
}
