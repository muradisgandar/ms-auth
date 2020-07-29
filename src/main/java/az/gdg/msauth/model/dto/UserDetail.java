package az.gdg.msauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetail {

    private Long id;
    private String firstName;
    private String lastName;
    private String imageUrl;

}
