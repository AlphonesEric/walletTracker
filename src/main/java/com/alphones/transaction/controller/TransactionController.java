package com.alphones.transaction.controller;

import com.alphones.transaction.service.ITransactionService;
import com.alphones.web.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/transaction"})
public class TransactionController {
    @Autowired
    private ITransactionService transactionService;

    @PostMapping({"/add/strategy"})
    public R<Boolean> addStrategy(@RequestParam String transactionType, @RequestParam String className) {
        this.transactionService.addStrategyTypeMap(transactionType, className);
        return R.ok();
    }

    @GetMapping({"/strategy/map"})
    public R<Map<String, String>> queryStrategy() {
        return R.ok(this.transactionService.strategyTypeMap());
    }
}
