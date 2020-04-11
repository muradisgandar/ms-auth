package az.gdg.msauth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {

    private List<String> mailTo;
    private String mailSubject;
    private String mailBody;
}
