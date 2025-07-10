package ru.axiomatika.batch_service.core.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.GroupValidationException;
import ru.axiomatika.batch_service.core.exception.ValidationException;
import ru.axiomatika.batch_service.core.validator.BatchItemValidator;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static ru.axiomatika.batch_service.core.exception.BaseExceptionCode.INVALID_XML_FORMAT;

@ExtendWith(MockitoExtension.class)
class BatchParserTest {

    @Mock
    private BatchItemValidator validator;

    @InjectMocks
    private BatchParser batchParser;

    private MultipartFile createTestZipFile(boolean withValidXml) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        
        ZipEntry entry = new ZipEntry("test.xml");
        zos.putNextEntry(entry);
        String xmlContent = withValidXml ? "<valid>xml</valid>" : "<invalid>xml</invalid>";
        zos.write(xmlContent.getBytes(StandardCharsets.UTF_8));
        zos.closeEntry();
        zos.close();

        return new MockMultipartFile(
                "test.zip",
                "test.zip",
                "application/zip",
                new ByteArrayInputStream(baos.toByteArray())
        );
    }

    @Test
    void toXmlFiles_ShouldReturnXmlFiles_WhenZipContainsValidXml() throws IOException {
        MultipartFile zipFile = createTestZipFile(true);
        when(validator.validateXml(anyString())).thenReturn(Collections.emptyList());

        List<XmlFileDto> result = batchParser.toXmlFiles(zipFile);

        assertEquals(1, result.size());
        assertEquals("test.xml", result.get(0).getName());
        assertEquals("<valid>xml</valid>", result.get(0).getXmlData());
    }

    @Test
    void toXmlFiles_ShouldThrowGroupValidationException_WhenXmlInvalid() throws IOException {
        MultipartFile zipFile = createTestZipFile(false);
        when(validator.validateXml(anyString())).thenReturn(List.of(
                new ValidationException(INVALID_XML_FORMAT, "Invalid XML format")
        ));

        assertThrows(GroupValidationException.class, () -> batchParser.toXmlFiles(zipFile));
    }

    @Test
    void toXmlFiles_ShouldThrowException_WhenInvalidZipFile() {
        MultipartFile invalidZip = new MockMultipartFile(
                "invalid.zip",
                "invalid.zip",
                "application/zip",
                new byte[0]
        );

        assertThrows(BaseException.class, () -> batchParser.toXmlFiles(invalidZip));
    }
}