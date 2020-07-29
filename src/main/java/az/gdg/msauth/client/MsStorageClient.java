package az.gdg.msauth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(value = "ms-storage-client", url = "${service.url.ms-storage}")
public interface MsStorageClient {

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String uploadFile(@RequestParam String folderName,
                      @RequestPart List<MultipartFile> multipartFile);
}
