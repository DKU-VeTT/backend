package kr.ac.dankook.VettChatServer.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateChatRoomRequest {

    @NotBlank(message = "Title is Required.")
    @Size(min=2,max=50,message="Title must be between 2 and 50 characters.")
    private String title;

    @NotBlank(message = "MemberId( Encryption Id ) is required.")
    private String memberId;
}
