package com.alphones.token.controller;

import com.alphones.token.entity.Token;
import com.alphones.token.service.ITokenService;
import com.alphones.web.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/token"})
public class TokenController {
    @Autowired
    private ITokenService tokenService;

    @GetMapping({"/list"})
    public R<List<Token>> queryTokenList() {
        return R.ok(this.tokenService.queryTokenList());
    }

    @PostMapping({"/add"})
    public R addToken(@RequestBody Token token) {
        this.tokenService.saveOrUpdate(token);
        return R.ok();
    }

    @DeleteMapping({"/delete/{address}"})
    public R addToken(@PathVariable String address) {
        this.tokenService.deleteToken(address);
        return R.ok();
    }
}
