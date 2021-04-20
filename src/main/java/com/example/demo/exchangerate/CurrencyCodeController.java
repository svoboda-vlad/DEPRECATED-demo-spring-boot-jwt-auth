package com.example.demo.exchangerate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CurrencyCodeController {
	
    private final CurrencyCodeRepository currencyCodeRepository;
    private static final String CURRENCY_CODE_URL = "/currency-code";

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping(CURRENCY_CODE_URL)
    public ResponseEntity<List<CurrencyCode>> getAllCurrencyCodes() {
        return ResponseEntity.ok(currencyCodeRepository.findAll());
    }
	
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(CURRENCY_CODE_URL)
    public ResponseEntity<CurrencyCode> createCurrencyCode(@Valid @RequestBody CurrencyCode currencyCode) throws URISyntaxException {
    	CurrencyCode saved = currencyCodeRepository.save(currencyCode);
        return ResponseEntity
                .created(new URI(CURRENCY_CODE_URL + "/" + saved.getId()))
                .body(saved);
    }

}
