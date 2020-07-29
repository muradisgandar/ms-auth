package az.gdg.msauth.service;

import az.gdg.msauth.model.dto.MailDTO;

public interface MailService {

    void sendToQueue(MailDTO mailDTO);

}
