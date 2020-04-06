package az.gdg.msauth.service;

import az.gdg.msauth.model.dto.MailDTO;

public interface EmailService {

    void sendToQueue(MailDTO mailDTO);
}
