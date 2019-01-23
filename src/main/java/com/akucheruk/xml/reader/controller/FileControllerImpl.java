package com.akucheruk.xml.reader.controller;

import com.akucheruk.xml.reader.domain.FileExtension;
import com.akucheruk.xml.reader.domain.Transaction;
import com.akucheruk.xml.reader.exception.XmlReaderException;
import com.akucheruk.xml.reader.exception.XmlReaderFileNotFoundException;
import com.akucheruk.xml.reader.processor.FileProcessor;
import com.akucheruk.xml.reader.processor.SAXProcessor;
import com.akucheruk.xml.reader.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.List;

@RequestMapping("/transactions")
@Controller
public class FileControllerImpl implements FileController {
    private ApplicationContext applicationContext;
    private TransactionRepository transactionRepository;

    private static final String POINT_SYMBOL = ".";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileControllerImpl.class);

    @Autowired
    public FileControllerImpl(ApplicationContext applicationContext, TransactionRepository transactionRepository) {
        this.applicationContext = applicationContext;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file) {
        if (file == null) {
            throw new XmlReaderFileNotFoundException("File is null!");
        }

        LOGGER.info("Start parsing file with name: [{}]...", file.getOriginalFilename());
        FileProcessor fileProcessor = getFileProcessor(getFileExtension(file.getOriginalFilename()));

        try (InputStream inputStream = file.getInputStream()) {
            List<Transaction> transactions =  fileProcessor.getTransactions(inputStream);
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            transactions.forEach(transaction -> transaction.setCreatedDate(currentTime));

            LOGGER.info("Start saving transactions, size: {}", transactions.size() );
            Iterable<Transaction> savedTransactions = transactionRepository.saveAll(transactions);
            LOGGER.info("Successfully saved transactions!");

            if (LOGGER.isDebugEnabled()) {
                savedTransactions.forEach(transaction -> LOGGER.debug("Saved transaction info: [{}]", transaction));
            }
        } catch (IOException ioe) {
            throw new XmlReaderException(String.format("Failed to parse file with name: [%s], error: %s", file.getOriginalFilename(), ioe.getMessage()), ioe);
        }

        return "resultForm";
    }

    @Override
    @GetMapping("/")
    public String uploadFile() {
        return "uploadForm";
    }

    @ExceptionHandler(XmlReaderFileNotFoundException.class)
    public ResponseEntity<?> handleFileNotFoundException(XmlReaderFileNotFoundException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(XmlReaderException.class)
    public ResponseEntity<?> handleXmlReaderException(XmlReaderException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private FileExtension getFileExtension(@Nullable String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return null;
        }
        String fileExtension = fileName.substring(fileName.lastIndexOf(POINT_SYMBOL) + 1);

        FileExtension format;
        try {
            format = FileExtension.valueOf(fileExtension.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new XmlReaderException(String.format("Failed to transform file extension [%s] to FileExtension. Error: ", fileExtension), e);
        }
        LOGGER.info("File extension: [{}] for file: [{}]", format, fileName);

        return format;
    }

    private FileProcessor getFileProcessor(FileExtension extension) {
        FileProcessor processor = null;
        if (FileExtension.XML == extension) {
            processor = applicationContext.getBean(SAXProcessor.class);
        }
        if (processor == null) {
            throw new XmlReaderException("Not supported file type: " + extension);
        }

        LOGGER.info("{} was bind for [{}] type.", processor.getClass(), extension);

        return processor;
    }

}