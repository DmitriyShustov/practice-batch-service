package ru.axiomatika.batch_service.core.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;
import ru.axiomatika.batch_service.web.dto.response_service.ResponseDto;

@FeignClient(name = "response-service", url = "${response.service.url}")
public interface ResponseServiceApi {

    @PostMapping("/process")
    ResponseEntity<ResponseDto> processRequest(@RequestBody XmlFileDto request);

}
