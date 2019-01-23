package com.akucheruk.xml.reader.repository;

import com.akucheruk.xml.reader.domain.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
}
