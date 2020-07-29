package az.gdg.msauth.security.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String role;
    private String status;
    private String userId;
    private String mail;
}
