package com.bank.transaction.controller;

import com.bank.transaction.model.Transaction;
import com.bank.transaction.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(transactionService.recordTransaction(request));
    }

    @PostMapping("/failure")
    public ResponseEntity<String> toggleFailure(@RequestParam boolean enabled) {
        transactionService.setFailMode(enabled);
        return ResponseEntity.ok("Fail mode set to " + enabled);
    }

    @PostMapping("/slow")
    public ResponseEntity<String> toggleSlow(@RequestParam boolean enabled) {
        transactionService.setSlowMode(enabled);
        return ResponseEntity.ok("Slow mode set to " + enabled);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getStatus() {
        return ResponseEntity.ok(Map.of(
            "failMode", transactionService.isFailMode(),
            "slowMode", transactionService.isSlowMode()
        ));
    }
}
