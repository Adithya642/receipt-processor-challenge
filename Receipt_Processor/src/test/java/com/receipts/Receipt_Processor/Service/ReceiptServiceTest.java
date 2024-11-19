package com.receipts.Receipt_Processor.Service;

import com.receipts.Receipt_Processor.Repository.ReceiptRepository;
import com.receipts.Receipt_Processor.model.Item;
import com.receipts.Receipt_Processor.model.Receipt;
import com.receipts.Receipt_Processor.model.ReceiptId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReceiptServiceTest {
    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private ReceiptService receiptService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Helper method to create a valid Receipt object
    private Receipt createValidReceipt() {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(UUID.randomUUID());
        receipt.setRetailer("Valid Retailer");
        receipt.setPurchaseDate(LocalDate.now());
        receipt.setPurchaseTime(LocalTime.of(15, 0));
        receipt.setItems(List.of(new Item("Valid Item", BigDecimal.valueOf(10))));
        receipt.setTotal(BigDecimal.valueOf(10));
        return receipt;
    }
    private Receipt createValidReceipt2() {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(UUID.randomUUID());
        receipt.setRetailer("Valid Retailer");
        receipt.setPurchaseDate(LocalDate.now());
        receipt.setPurchaseTime(LocalTime.of(15, 0));
//        receipt.setItems(List.of(new Item("Valid Item", BigDecimal.valueOf(10))));
        receipt.setTotal(BigDecimal.valueOf(10));
        return receipt;
    }

    // Helper method to create an invalid Receipt object
    private Receipt createInvalidReceipt() {
        Receipt receipt = new Receipt();
        receipt.setReceiptId(UUID.randomUUID());
        // No retailer, no items, invalid total, etc.
        return receipt;
    }

    @Test
    void processReceipt_shouldReturnCreatedWhenValidReceipt() {
        // Arrange
        Receipt receipt = createValidReceipt();
        when(receiptRepository.save(any(Receipt.class))).thenReturn(receipt);

        // Act
        ResponseEntity<?> response = receiptService.processReceipt(receipt);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReceiptId);
        verify(receiptRepository, times(1)).save(receipt);
    }

    @Test
    void processReceipt_shouldReturnBadRequestWhenMissingRetailer() {
        // Arrange
        Receipt receipt = createValidReceipt();
        receipt.setRetailer(null); // Missing retailer

        // Act
        ResponseEntity<?> response = receiptService.processReceipt(receipt);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Retailer name is required."));
        verify(receiptRepository, times(0)).save(receipt);
    }

    @Test
    void processReceipt_shouldReturnBadRequestWhenNoItems() {
        // Arrange
        Receipt receipt = createValidReceipt();
        receipt.setItems(null); // No items
       // Receipt receipt = new Receipt();
        // Act
        ResponseEntity<?> response = receiptService.processReceipt(receipt);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("The receipt is invalid: At least one item is required."));
        verify(receiptRepository, times(0)).save(receipt);
    }

    @Test
    void processReceipt_shouldReturnBadRequestWhenInvalidTotal() {
        // Arrange
        Receipt receipt = createValidReceipt();
        receipt.setTotal(BigDecimal.ZERO); // Invalid total

        // Act
        ResponseEntity<?> response = receiptService.processReceipt(receipt);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Total must be a positive value."));
        verify(receiptRepository, times(0)).save(receipt);
    }

    @Test
    void processReceipt_shouldReturnBadRequestWhenPurchaseDateInFuture() {
        // Arrange
        Receipt receipt = createValidReceipt();
        receipt.setPurchaseDate(LocalDate.now().plusDays(1)); // Future date

        // Act
        ResponseEntity<?> response = receiptService.processReceipt(receipt);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("Purchase date cannot be in the future."));
        verify(receiptRepository, times(0)).save(receipt);
    }

    @Test
    void findPoints_shouldReturnPointsWhenReceiptExists() {
        // Arrange
        Receipt receipt = createValidReceipt();
        when(receiptRepository.findById(receipt.getReceiptId())).thenReturn(Optional.of(receipt));

        // Act
        ResponseEntity<?> response = receiptService.findPoints(receipt.getReceiptId());

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody() instanceof ReceiptService.PointsResponse);
        ReceiptService.PointsResponse pointsResponse = (ReceiptService.PointsResponse) response.getBody();
        assertNotNull(pointsResponse);
        assertTrue(pointsResponse.getPoints() > 0);
    }

    @Test
    void findPoints_shouldReturnBadRequestWhenReceiptNotFound() {
        // Arrange
        UUID receiptId = UUID.randomUUID();
        when(receiptRepository.findById(receiptId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = receiptService.findPoints(receiptId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("No receipt found for that id"));
    }

    @Test
    void calculatePoints_shouldCalculatePointsCorrectly() {
        // Arrange
        Receipt receipt = createValidReceipt();

        // Act
        int points = receiptService.calculatePoints(receipt);

        // Assert
        assertTrue(points > 0);
        // Add specific checks for each rule if necessary
    }
}
