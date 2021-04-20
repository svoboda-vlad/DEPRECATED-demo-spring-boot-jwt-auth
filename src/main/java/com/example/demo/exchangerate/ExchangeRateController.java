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
public class ExchangeRateController {
	
    private final ExchangeRateRepository exchangeRateRepository;
    private static final String EXCHANGE_RATE_URL = "/exchange-rate";

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping(EXCHANGE_RATE_URL)
    public ResponseEntity<List<ExchangeRate>> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRateRepository.findAll());
    }
	
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PostMapping(EXCHANGE_RATE_URL)
    public ResponseEntity<ExchangeRate> createExchangeRate(@Valid @RequestBody ExchangeRate exchangeRate) throws URISyntaxException {
    	ExchangeRate saved = exchangeRateRepository.save(exchangeRate);
        return ResponseEntity
                .created(new URI(EXCHANGE_RATE_URL + "/" + saved.getId()))
                .body(saved);
    }

}
