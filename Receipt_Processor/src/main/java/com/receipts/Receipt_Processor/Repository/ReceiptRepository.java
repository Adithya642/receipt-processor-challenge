package com.receipts.Receipt_Processor.Repository;

import com.receipts.Receipt_Processor.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID> {

}
