package az.gdg.msauth.config;

import az.gdg.msauth.client.MsStorageClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableFeignClients(clients = MsStorageClient.class)
public class FeignConfig {

}
