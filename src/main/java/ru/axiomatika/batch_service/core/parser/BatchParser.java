package ru.axiomatika.batch_service.core.parser;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.GroupValidationException;
import ru.axiomatika.batch_service.core.exception.ValidationException;
import ru.axiomatika.batch_service.core.validator.BatchItemValidator;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
@AllArgsConstructor
public class BatchParser {

    private final BatchItemValidator batchItemValidator;

    private List<ValidationException> errors;

    public List<XmlFileDto> toXmlFiles(MultipartFile file) {
        this.errors = new ArrayList<>();
        List<XmlFileDto> xmlFiles = new ArrayList<>();

        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    String xmlContent = new String(zipInputStream.readAllBytes(), StandardCharsets.UTF_8);

                    errors.addAll(batchItemValidator.validateXml(xmlContent));
                    xmlFiles.add(XmlFileDto.builder()
                                    .name(zipEntry.getName())
                                    .xmlData(xmlContent)
                                    .build());
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new BaseException(
                    e.getMessage(),
                    BaseExceptionCode.INTERNAL_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        if (!errors.isEmpty()) {
            throw new GroupValidationException(errors);
        }

        return xmlFiles;
    }

}
