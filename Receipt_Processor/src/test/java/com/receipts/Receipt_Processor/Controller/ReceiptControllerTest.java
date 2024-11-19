package com.receipts.Receipt_Processor.Controller;

import com.receipts.Receipt_Processor.Service.ReceiptService;
import com.receipts.Receipt_Processor.model.Item;
import com.receipts.Receipt_Processor.model.Receipt;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReceiptController.class)
public class ReceiptControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReceiptService receiptService;
    //private ReceiptService receiptService;
    @Test
    public void testProcessReceipt_ValidRequest_ReturnsCreated() throws Exception {
        // JSON payload with all required fields
        String jsonPayload = """
        {
            "retailer": "Test Retailer",
            "total": 3.5,
            "items": [
                {
                    "shortDescription": "coke",
                    "price": 3.5
                }
            ]
        }
        """;

        when(receiptService.processReceipt(Mockito.any(Receipt.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.CREATED));

        mockMvc.perform(post("/receipts/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated());
    }


    @Test
    public void testCalculatePoints_InvalidUUIDFormat_ReturnsBadRequest() throws Exception {
        // Perform the request and capture the response
        MvcResult result = mockMvc.perform(get("/receipts/invalid-uuid/points"))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Extract the response content
        String responseContent = result.getResponse().getContentAsString();

        // Assert the response JSON
        String expectedResponse = "{\"error\":\"Invalid UUID format\",\"message\":\"Invalid UUID format for id: invalid-uuid\"}";
        assertEquals(expectedResponse, responseContent);
    }

}
