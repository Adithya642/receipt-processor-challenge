package com.receipts.Receipt_Processor.Service;

import com.receipts.Receipt_Processor.Repository.ReceiptRepository;
import com.receipts.Receipt_Processor.model.Item;
import com.receipts.Receipt_Processor.model.Receipt;
import com.receipts.Receipt_Processor.model.ReceiptId;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ReceiptService {
    private final ReceiptRepository receiptRepository;

    public ReceiptService(ReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    public ReceiptId generateId( Receipt receipt) {
        receiptRepository.save(receipt);
        return new ReceiptId(receipt.getReceiptId());
    }

    public ResponseEntity<?> processReceipt(Receipt receipt) {
        String errorString = "The receipt is invalid: ";
        String validationError = validateReceipt(receipt);

        if (validationError != null) {
            return new ResponseEntity<>(errorString + validationError, HttpStatus.BAD_REQUEST);
        }

        // Save the receipt if valid
        Receipt savedReceipt = receiptRepository.save(receipt);
        return new ResponseEntity<>(new ReceiptId(savedReceipt.getReceiptId()), HttpStatus.CREATED);
    }

    private String validateReceipt(Receipt receipt) {
        if (receipt.getRetailer() == null || receipt.getRetailer().trim().isEmpty()) {
            return "Retailer name is required.";
        }

        if (receipt.getItems() == null || receipt.getItems().isEmpty()) {
            return "At least one item is required.";
        }

        for (Item item : receipt.getItems()) {
            if (item.getShortDescription() == null || item.getShortDescription().trim().isEmpty()) {
                return "Each item must have a description.";
            }
            if (item.getPrice() == null || item.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                return "Each item must have a positive price.";
            }
        }

        if (receipt.getTotal() == null || receipt.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            return "Total must be a positive value.";
        }

        if (receipt.getPurchaseDate() != null && receipt.getPurchaseDate().isAfter(LocalDate.now())) {
            return "Purchase date cannot be in the future.";
        }
        if (receipt.getPurchaseTime() != null && receipt.getPurchaseDate() == null) {
            return "Purchase date must be provided if time is specified.";
        }

        return null; // No validation errors
    }

    public ResponseEntity<?> findPoints(UUID id) {
        Optional<Receipt> receiptOptional = receiptRepository.findById(id);

        if (receiptOptional.isEmpty()) {
            String errorString = "No receipt found for that id";
            return new ResponseEntity<>(errorString, HttpStatus.BAD_REQUEST);
        }

        Receipt receipt = receiptOptional.get();
        int points = calculatePoints(receipt);
        return new ResponseEntity<>(new PointsResponse(points), HttpStatus.OK);
    }

    public int calculatePoints(Receipt receipt) {
        int points = 0;

        // Rule 1: One point for every alphanumeric character in the retailer name
        String retailer = receipt.getRetailer();
        points += countAlphanumericCharacters(retailer);

        // Rule 2: 50 points if the total is a round dollar amount with no cents
        BigDecimal totalAmount = receipt.getTotal();
        if (isRoundDollarAmount(totalAmount)) {
            points += 50;
        }

        // Rule 3: 25 points if the total is a multiple of 0.25
        if (totalAmount.remainder(BigDecimal.valueOf(0.25)).compareTo(BigDecimal.ZERO) == 0) {
            points += 25;
        }

        // Rule 4: 5 points for every two items on the receipt
        int itemCount = receipt.getItems().size();
        points += (itemCount / 2) * 5;

        // Rule 5: Points based on item description length
        for (Item item : receipt.getItems()) {
            String description = item.getShortDescription().trim();
            if (description.length() % 3 == 0) {
                BigDecimal price = item.getPrice();
                points += price.multiply(BigDecimal.valueOf(0.2)).setScale(0, BigDecimal.ROUND_UP).intValue();
            }
        }

        // Rule 6: 6 points if the day in the purchase date is odd
        LocalDate purchaseDate = receipt.getPurchaseDate();
        if (purchaseDate != null && purchaseDate.getDayOfMonth() % 2 != 0) {
            points += 6;
        }

        // Rule 7: 10 points if the time of purchase is after 2:00 pm and before 4:00 pm
        LocalTime purchaseTime = receipt.getPurchaseTime();
        if (purchaseTime != null && purchaseTime.isAfter(LocalTime.of(14, 0)) && purchaseTime.isBefore(LocalTime.of(16, 0))) {
            points += 10;
        }

        return points;
    }

    private int countAlphanumericCharacters(String str) {
        int count = 0;
        for (char c : str.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                count++;
            }
        }
        return count;
    }

    private boolean isRoundDollarAmount(BigDecimal amount) {
        return amount.scale() == 0 || amount.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0;
    }

    // Points response class to handle JSON output
    public static class PointsResponse {
        private int points;

        public PointsResponse(int points) {
            this.points = points;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }
}
