package com.akucheruk.xml.reader.processor;

import com.akucheruk.xml.reader.domain.Transaction;
import com.akucheruk.xml.reader.exception.XmlReaderException;
import com.akucheruk.xml.reader.handler.TransactionHandler;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockMultipartFile;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SAXProcessor.class)
public class SAXProcessorTest {

    @Spy
    private SAXProcessor processor = new SAXProcessor(new TransactionHandler());

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private static final String GET_PARSER_METHOD = "getParser";

    @Test
    public void getTransactionsWithIllegalArgumentExceptionTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to parse transactions from inputStream. Error message: some error, Error: ");
        InputStream inputStream = mock(InputStream.class);
        PowerMockito.doThrow(new IllegalArgumentException("some error")).when(processor, GET_PARSER_METHOD);

        processor.getTransactions(inputStream);
    }

    @Test
    public void getTransactionsWithIOExceptionTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to parse transactions from inputStream. Error message: some error, Error: ");

        SAXParser parser = mock(SAXParser.class);
        PowerMockito.when(processor, GET_PARSER_METHOD).thenReturn(parser);
        doThrow(new IOException("some error")).when(parser).parse(any(InputStream.class), any(DefaultHandler.class));

        processor.getTransactions(mock(InputStream.class));
        verify(parser).parse(any(InputStream.class), any(DefaultHandler.class));
    }

    @Test
    public void getTransactionsWithParserConfigurationExceptionTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to configure parser or got exception during parsing inputStream. Error message: some error, Error: ");
        PowerMockito.doThrow(new ParserConfigurationException("some error")).when(processor, GET_PARSER_METHOD);

        processor.getTransactions(mock(InputStream.class));
    }

    @Test
    public void getTransactionsWithSAXExceptionTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to configure parser or got exception during parsing inputStream. Error message: some error, Error: ");
        PowerMockito.doThrow(new SAXException("some error")).when(processor, GET_PARSER_METHOD);

        processor.getTransactions(mock(InputStream.class));
    }

    @Test(timeout = 3000)
    public void getTransactionsWithRightFileXmlTest() throws Exception {
        File xmlFile = new File("src/test/resources/files/right_xml_file.xml");
        FileInputStream input = new FileInputStream(xmlFile);
        MockMultipartFile multipartFile = spy(new MockMultipartFile(xmlFile.getName(), xmlFile.getName(), "text/plain", input));

        List<Transaction> transactions = processor.getTransactions(multipartFile.getInputStream());

        assertTrue(transactions.size() == 12);
        PowerMockito.verifyPrivate(processor).invoke(GET_PARSER_METHOD);
    }

    @Test
    public void getTransactionsWithRightAndEmptyTransactionsXmlFileTest() throws Exception {
        File xmlFile = new File("src/test/resources/files/right_and_empty_xml_file.xml");
        FileInputStream input = new FileInputStream(xmlFile);
        MockMultipartFile multipartFile = spy(new MockMultipartFile(xmlFile.getName(), xmlFile.getName(), "text/plain", input));

        List<Transaction> transactions = processor.getTransactions(multipartFile.getInputStream());

        assertTrue(transactions.isEmpty());
        PowerMockito.verifyPrivate(processor).invoke(GET_PARSER_METHOD);
    }

    @Test
    public void getTransactionsWithAnotherXmlFileTest() throws Exception {
        File xmlFile = new File("src/test/resources/files/another_xml_file.xml");
        FileInputStream input = new FileInputStream(xmlFile);
        MockMultipartFile multipartFile = spy(new MockMultipartFile(xmlFile.getName(), xmlFile.getName(), "text/plain", input));

        List<Transaction> transactions = processor.getTransactions(multipartFile.getInputStream());

        assertTrue(transactions.isEmpty());
        PowerMockito.verifyPrivate(processor).invoke(GET_PARSER_METHOD);
    }

    @Test
    public void getTransactionsWithJsonFileTest() throws Exception {
        File xmlFile = new File("src/test/resources/files/json_file.json");
        FileInputStream input = new FileInputStream(xmlFile);
        MockMultipartFile multipartFile = spy(new MockMultipartFile(xmlFile.getName(), xmlFile.getName(), "text/plain", input));

        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to configure parser or got exception during parsing inputStream. Error message: Content is not allowed in prolog., Error:");

        List<Transaction> transactions = processor.getTransactions(multipartFile.getInputStream());

        assertTrue(transactions.isEmpty());
        PowerMockito.verifyPrivate(processor).invoke(GET_PARSER_METHOD);
    }
}
