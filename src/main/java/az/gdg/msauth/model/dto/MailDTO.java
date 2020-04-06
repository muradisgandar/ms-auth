package az.gdg.msauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {

    private String mailTo;
    private String mailSubject;
    private String mailBody;
}
