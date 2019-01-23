package com.akucheruk.xml.reader.processor;

import com.akucheruk.xml.reader.domain.Transaction;

import java.io.InputStream;
import java.util.List;

public interface XMLProcessor extends FileProcessor{
    List<Transaction> getTransactions(InputStream inputStream);
}
