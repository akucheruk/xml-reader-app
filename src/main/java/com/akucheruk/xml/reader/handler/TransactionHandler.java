package com.akucheruk.xml.reader.handler;

import com.akucheruk.xml.reader.domain.Client;
import com.akucheruk.xml.reader.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.akucheruk.xml.reader.config.Constants.Client.*;
import static com.akucheruk.xml.reader.config.Constants.Transaction.*;

@Component
public class TransactionHandler extends DefaultHandler {
    private Client client;
    private Transaction transaction;
    private List<Transaction> transactions = new ArrayList<>();
    private StringBuilder data = null;

    private boolean bPlace = false;
    private boolean bAmount = false;
    private boolean bCurrency = false;
    private boolean bCard = false;

    private boolean bFirstName = false;
    private boolean bLastName = false;
    private boolean bMiddleName = false;
    private boolean bInn = false;

    private static final String failedMsg = "Failed to convert [%s] to BigDecimal, transaction info: [%s]";
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHandler.class);

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (PLACE_TAG.equalsIgnoreCase(qName)) {
            transaction = new Transaction();
            bPlace = true;
        } else if (AMOUNT_TAG.equalsIgnoreCase(qName)) {
            bAmount = true;
        } else if (CURRENCY_TAG.equalsIgnoreCase(qName)) {
            bCurrency = true;
        } else if (CARD_TAG.equalsIgnoreCase(qName)) {
            bCard = true;
        } else if (FIRST_NAME_TAG.equalsIgnoreCase(qName)) {
            client = new Client();
            bFirstName = true;
        } else if (LAST_NAME_TAG.equalsIgnoreCase(qName)) {
            bLastName = true;
        } else if (MIDDLE_NAME_TAG.equalsIgnoreCase(qName)) {
            bMiddleName = true;
        } else if (INN_TAG.equalsIgnoreCase(qName)) {
            bInn = true;
        }
        data = new StringBuilder();
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            if (bPlace) {
                bPlace = false;
                transaction.setPlace(data.toString());
            } else if (bCurrency) {
                bCurrency = false;
                transaction.setCurrency(data.toString());
            } else if (bCard) {
                bCard = false;
                transaction.setCard(data.toString());
            } else if (bFirstName) {
                bFirstName = false;
                client.setFirstName(data.toString());
            } else if (bLastName) {
                bLastName = false;
                client.setLastName(data.toString());
            } else if (bMiddleName) {
                bMiddleName = false;
                client.setMiddleName(data.toString());
            } else if (bInn) {
                bInn = false;
                client.setInn(Long.valueOf(data.toString()));
            } else if (bAmount) {
                bAmount = false;
                transaction.setAmount(new BigDecimal(data.toString()));
            }

            if (TRANSACTION_TAG.equalsIgnoreCase(qName)) {
                transaction.setClient(client);
                transactions.add(transaction);
                LOGGER.info("Parsed transaction: [{}]", transaction);
            }
        } catch (NumberFormatException nfe) {
            LOGGER.warn(String.format(failedMsg, data, transaction));
        }
    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {
        data.append(new String(ch, start, length));
    }
}
