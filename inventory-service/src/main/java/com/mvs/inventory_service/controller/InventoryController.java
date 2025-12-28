package com.mvs.inventory_service.controller;

import com.mvs.inventory_service.dto.InventoryDto;
import com.mvs.inventory_service.dto.StockAdjustmentRequest;
import com.mvs.inventory_service.enums.StockOperation;
import com.mvs.inventory_service.model.Inventory;
import com.mvs.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/products/{id}")
    public ResponseEntity<InventoryDto> getStock(@PathVariable("id") String productId) {
        return ResponseEntity.ok(InventoryDto.init(inventoryService.getByProductId(productId)));
    }

    @GetMapping("/products/{id}/available")
    public ResponseEntity<Boolean> checkAvailability(@PathVariable("id") String productId,
                                                     @RequestParam int qty) {
        return ResponseEntity.ok(inventoryService.isAvailable(productId, qty));
    }

    @PostMapping("products/{id}/adjust")
    public ResponseEntity<InventoryDto> adjustStock(@PathVariable("id") String productId,
                                            @RequestBody @Valid StockAdjustmentRequest request) {
        Inventory inventory;
        if (StockOperation.INCREASE.equals(request.getOperation())) {
            inventory = inventoryService.increaseStock(productId, request.getQuantity());
        } else if (StockOperation.DECREASE.equals(request.getOperation())) {
            inventory = inventoryService.reduceStock(productId, request.getQuantity());
        } else {
            throw new BadRequestException("Invalid operation");
        }
        return ResponseEntity.ok(InventoryDto.init(inventory));
    }
}
