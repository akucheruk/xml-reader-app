package com.akucheruk.xml.reader.controller;

import com.akucheruk.xml.reader.domain.FileExtension;
import com.akucheruk.xml.reader.exception.XmlReaderException;
import com.akucheruk.xml.reader.exception.XmlReaderFileNotFoundException;
import com.akucheruk.xml.reader.processor.FileProcessor;
import com.akucheruk.xml.reader.processor.SAXProcessor;
import com.akucheruk.xml.reader.repository.TransactionRepository;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.reflect.Whitebox;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@PrepareForTest(FileControllerImpl.class)
@RunWith(MockitoJUnitRunner.class)
public class FileControllerImplTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private FileControllerImpl controller;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    private MultipartFile rightMultipartFile;

    private static final String GET_FILE_EXTENSION_METHOD = "getFileExtension";
    private static final String GET_FILE_PROCESSOR_METHOD = "getFileProcessor";

    @Before
    public void init() throws IOException {
        File xmlFile = new File("src/test/resources/files/right_xml_file.xml");
        FileInputStream input = new FileInputStream(xmlFile);
        rightMultipartFile = spy(new MockMultipartFile(xmlFile.getName(), xmlFile.getName(), "text/plain", input));
    }

    @Test
    public void handleFileUploadWithNullFileTest() {
        expectedEx.expect(XmlReaderFileNotFoundException.class);
        expectedEx.expectMessage("File is null!");

        controller.handleFileUpload(null);
    }

    @Test
    public void handleFileUploadWithIOExceptionTest() throws IOException {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to parse file with name: [right_xml_file.xml], error: some error");

        SAXProcessor saxProcessor = mock(SAXProcessor.class);
        when(applicationContext.getBean(SAXProcessor.class)).thenReturn(saxProcessor);

        when(rightMultipartFile.getInputStream()).thenThrow(new IOException("some error"));

        controller.handleFileUpload(rightMultipartFile);
    }

    @Test
    public void handleFileUploadTest() throws IOException {
        SAXProcessor saxProcessor = mock(SAXProcessor.class);
        when(saxProcessor.getTransactions(any(InputStream.class))).thenReturn(mock(List.class));
        when(applicationContext.getBean(SAXProcessor.class)).thenReturn(saxProcessor);

        controller.handleFileUpload(rightMultipartFile);

        verify(saxProcessor).getTransactions(any(InputStream.class));
        verify(transactionRepository).saveAll(any(List.class));
    }

    @Test
    public void getFileExtensionWithNullFileNameTest() throws Exception {
        FileExtension result = Whitebox.invokeMethod(controller, GET_FILE_EXTENSION_METHOD, null);
        assertNull(result);
    }

    @Test
    public void getFileExtensionWithEmptyFileNameTest() throws Exception {
        assertNull(Whitebox.invokeMethod(controller, GET_FILE_EXTENSION_METHOD, ""));
    }

    @Test
    public void getFileExtensionWithWrongFileNameFormatTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Failed to transform file extension [some1_file] to FileExtension. Error:");

        FileExtension result = Whitebox.invokeMethod(controller, GET_FILE_EXTENSION_METHOD, "some1_file");
        assertNull(result);
    }

    @Test
    public void getFileExtension() throws Exception {
        FileExtension result = Whitebox.invokeMethod(controller, GET_FILE_EXTENSION_METHOD, "some1_file.xml");
        assertTrue(FileExtension.XML == result);
    }

    @Test
    public void getFileProcessorWithNullFileExtensionTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Not supported file type: ");

        Whitebox.invokeMethod(controller, GET_FILE_PROCESSOR_METHOD, null);
    }

    @Test
    public void getFileProcessorWithWrongFileExtensionTest() throws Exception {
        expectedEx.expect(XmlReaderException.class);
        expectedEx.expectMessage("Not supported file type: ");
        when(applicationContext.getBean(SAXProcessor.class)).thenReturn(null);

        Whitebox.invokeMethod(controller, GET_FILE_PROCESSOR_METHOD, FileExtension.XML);
    }

    @Test
    public void getFileProcessorTest() throws Exception {
        when(applicationContext.getBean(SAXProcessor.class)).thenReturn(mock(SAXProcessor.class));
        FileProcessor fileProcessor = Whitebox.invokeMethod(controller, GET_FILE_PROCESSOR_METHOD, FileExtension.XML);

        assertTrue(fileProcessor instanceof SAXProcessor);
    }
}
