package com.receipts.Receipt_Processor.model;

import com.receipts.Receipt_Processor.model.Item;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Receipt {

    @Id
    @GeneratedValue
    private UUID receiptId;

    @NotNull
    private String retailer;

    private LocalDate purchaseDate;

    private LocalTime purchaseTime;


//    @Size(min = 1)
    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items = new ArrayList<>();

    @NotNull
    private BigDecimal total;

    // Set receipt reference for each item when setting items
    public void setItems(List<Item> items) {
//        if (items == null || items.isEmpty()) {
//            throw new IllegalArgumentException("Items list cannot be null or empty.");
//        }
        if(items!=null)
        for (Item item : items) {
            item.setReceipt(this);  // Ensure each item has the reference to this receipt
        }
        this.items = items;
    }

    public Receipt() {
    }

    public Receipt(UUID receiptId, String retailer, LocalDate purchaseDate, LocalTime purchaseTime, List<Item> items, BigDecimal total) {
        this.receiptId = receiptId;
        this.retailer = retailer;
        this.purchaseDate = purchaseDate;
        this.purchaseTime = purchaseTime;
        this.items = items;
        this.total = total;
    }

    // Getters and Setters
    public UUID getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(UUID receiptId) {
        this.receiptId = receiptId;
    }

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public LocalTime getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(LocalTime purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public List<Item> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}
