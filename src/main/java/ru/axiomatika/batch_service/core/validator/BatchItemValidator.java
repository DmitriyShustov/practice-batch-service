package ru.axiomatika.batch_service.core.validator;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class BatchItemValidator {

    private static final List<String> REQUIRED_XML_FIELDS = List.of(
            "/request"
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
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.parse(new InputSource(new StringReader(xmlContent)));
        } catch (ParserConfigurationException | SAXException e) {
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
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlContent)));

            XPathFactory xPathFactory = XPathFactory.newInstance();
            XPath xpath = xPathFactory.newXPath();

            for (String xpathExpression : REQUIRED_XML_FIELDS) {
                XPathExpression expression = xpath.compile(xpathExpression);
                NodeList nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

                if (nodes == null || nodes.getLength() == 0) {
                    String fieldName = xpathExpression.substring(xpathExpression.lastIndexOf('/') + 1);
                    errors.add(new ValidationException(
                            BaseExceptionCode.INVALID_XML_MISSING_REQUIRED_FIELDS,
                            "Missing required XML field: " + fieldName
                    ));
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_XML_FORMAT,
                    "Error parsing XML: " + e.getMessage()
            ));
        } catch (XPathExpressionException e) {
            errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_XML_FORMAT,
                    "Error evaluating XPath expression: " + e.getMessage()
            ));
        }
    }

}