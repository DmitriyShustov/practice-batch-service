package ru.axiomatika.batch_service.core.validator;

import org.springframework.stereotype.Component;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class BatchItemValidator {

    private static final List<String> REQUIRED_XML_FIELDS = List.of(
            "request"
    );

      public List<ValidationException> validateXml(String xmlContent) throws ValidationException {
        List<ValidationException> errors = new ArrayList<>();

        validateXmlNotEmpty(xmlContent, errors);
        if (errors.isEmpty()) {
            validateXmlStructure(xmlContent, errors);
        }
        if (errors.isEmpty()) {
            validateXmlContent(xmlContent, errors);
        }

        return errors;
    }

    private void validateXmlNotEmpty(String xmlContent, List<ValidationException> errors) throws ValidationException {
        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_XML_EMPTY,
                    "XML content is empty"
            ));
        }
    }

    private void validateXmlStructure(String xmlContent, List<ValidationException> errors) throws ValidationException {
        try {
            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.parse(new InputSource(new StringReader(xmlContent)));
        } catch (SAXException e) {
            errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_XML_FORMAT,
                    "Invalid XML format: " + e.getMessage()
            ));
        } catch (IOException e) {
            errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_XML_FORMAT,
                    "Error reading XML: " + e.getMessage()
            ));
        }
    }

    private void validateXmlContent(String xmlContent, List<ValidationException> errors) throws ValidationException {
        for (String field : REQUIRED_XML_FIELDS) {
            if (!xmlContent.contains("<" + field + ">") || !xmlContent.contains("</" + field + ">")) {
                errors.add(new ValidationException(
                        BaseExceptionCode.INVALID_XML_MISSING_REQUIRED_FIELDS,
                        "Missing required XML field: " + field
                ));
            }
        }
    }

}