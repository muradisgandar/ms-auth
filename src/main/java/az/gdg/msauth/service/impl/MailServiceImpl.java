package az.gdg.msauth.service.impl;

import az.gdg.msauth.model.dto.MailDTO;
import az.gdg.msauth.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


@Service
@EnableBinding(Source.class)
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);
    private final Source source;

    public MailServiceImpl(Source source) {
        this.source = source;
    }

    @Override
    public void sendToQueue(MailDTO mailDTO) {
        source.output().send(MessageBuilder.withPayload(mailDTO).build());
    }

}
