package com.akucheruk.xml.reader.processor;

import com.akucheruk.xml.reader.domain.Transaction;
import com.akucheruk.xml.reader.exception.XmlReaderException;
import com.akucheruk.xml.reader.handler.TransactionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.akucheruk.xml.reader.processor.SAXProcessor.SAX_PROCESSOR;

@Component(SAX_PROCESSOR)
public class SAXProcessor implements XMLProcessor {

    private SAXParser parser;
    private TransactionHandler transactionHandler;

    public static final String SAX_PROCESSOR = "saxProcessor";
    private static final Logger LOGGER = LoggerFactory.getLogger(SAXProcessor.class);

    @Autowired
    public SAXProcessor(TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    private SAXParser getParser() throws ParserConfigurationException, SAXException {
        if (parser == null) {
            synchronized (SAXProcessor.class) {
                if (parser == null) {
                    SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                    this.parser = saxParserFactory.newSAXParser();
                }
            }
        }
        return parser;
    }

    @Override
    public List<Transaction> getTransactions(InputStream inputStream) {
        LOGGER.info("Start parsing transactions...");
        try {
            getParser().parse(inputStream, transactionHandler);
        } catch (IOException | IllegalArgumentException e) {
            throw new XmlReaderException(String.format("Failed to parse transactions from inputStream. Error message: %s, Error: ", e.getMessage()), e);
        } catch (ParserConfigurationException | SAXException e) {
            throw new XmlReaderException(String.format("Failed to configure parser or got exception during parsing inputStream. Error message: %s, Error: ", e.getMessage()), e);
        }

        LOGGER.info("Transactions were parsed successfully. Size: " + transactionHandler.getTransactions().size());

        return transactionHandler.getTransactions();
    }
}
