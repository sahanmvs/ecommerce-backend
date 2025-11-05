package com.mvs.inventory_service.controller;

import com.mvs.inventory_service.dto.InventoryDto;
import com.mvs.inventory_service.dto.StockAdjustmentRequest;
import com.mvs.inventory_service.service.InventoryService;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/product/{id}")
    public ResponseEntity<InventoryDto> getStock(@PathVariable("id") String productId) {
        return ResponseEntity.ok(InventoryDto.init(inventoryService.getByProductId(productId)));
    }

    @GetMapping("/product/{id}/available")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable("id") String productId,
                                                     @RequestParam int qty) {
        return ResponseEntity.ok(inventoryService.isAvailable(productId, qty));
    }

    @PostMapping("product/{id}/adjust")
    public ResponseEntity<Void> adjustStock(@PathVariable("id") String productId,
                                            @RequestBody StockAdjustmentRequest request) {
        if ("INCREASE".equalsIgnoreCase(request.getOperation())) {
            inventoryService.increaseStock(productId, request.getQuantity());
        } else if ("DECREASE".equalsIgnoreCase(request.getOperation())) {
            inventoryService.reduceStock(productId, request.getQuantity());
        } else {
            throw new BadRequestException("Invalid operation");
        }
        return ResponseEntity.ok().build();
    }
}
