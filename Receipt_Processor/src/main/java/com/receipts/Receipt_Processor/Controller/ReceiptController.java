package com.receipts.Receipt_Processor.Controller;

import com.receipts.Receipt_Processor.Exception.InvalidUUIDFormatException;
import com.receipts.Receipt_Processor.Service.ReceiptService;
import com.receipts.Receipt_Processor.model.Receipt;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class ReceiptController {


    private final ReceiptService receiptService;

    public ReceiptController(ReceiptService receiptService){
        this.receiptService=receiptService;
    }

    @PostMapping("/receipts/process")
    public ResponseEntity<?> processReceipt(@Valid @RequestBody Receipt receipt){
         // return ;
         return receiptService.processReceipt(receipt);
    }
    @GetMapping("/receipts/{id}/points")
    public ResponseEntity<?> calculatePoints(@PathVariable String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);  // Attempt to parse the UUID
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDFormatException("Invalid UUID format for id: " + id);
        }
        return receiptService.findPoints(uuid);
    }
}
