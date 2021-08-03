package com.example.demo.exchangerate;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ExchangeRateController {
	
    private final ExchangeRateRepository exchangeRateRepository;
    private final CurrencyCodeRepository currencyCodeRepository;    
    private static final String EXCHANGE_RATE_URL = "/exchange-rate";
    private static final String CURRENCY_CODE_URL = "/currency-code";
    private static final String EXCHANGE_RATE_BY_DATE_URL = "/exchange-rate/date";    

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
    
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping(EXCHANGE_RATE_URL + CURRENCY_CODE_URL + "/{id}")
    public ResponseEntity<List<ExchangeRate>> getExchangeRatesByCurrencyCode(@PathVariable Long id) {
		Optional<CurrencyCode> currencyCode = currencyCodeRepository.findById(id);
		if (currencyCode.isPresent()) {
			return ResponseEntity.ok(exchangeRateRepository.findByCurrencyCode(currencyCode.get()));
		}
		return ResponseEntity.notFound().build();
    }

    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping(EXCHANGE_RATE_BY_DATE_URL + "/{rateDate}")
    public ResponseEntity<List<ExchangeRate>> getExchangeRatesByRateDate(@PathVariable String rateDate) {
    	return ResponseEntity.ok(exchangeRateRepository.findByRateDate(LocalDate.parse(rateDate)));
    }
    
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @PutMapping(EXCHANGE_RATE_URL + "/{id}")
    public ResponseEntity<ExchangeRate> updateExchangeRate(@Valid @RequestBody ExchangeRate exchangeRate, @PathVariable long id) throws URISyntaxException {    	
        if (exchangeRate.getId() == 0L) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if (id != exchangeRate.getId()) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (exchangeRateRepository.findById(id).isEmpty()) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    	
    	ExchangeRate updated = exchangeRateRepository.save(exchangeRate);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @DeleteMapping(EXCHANGE_RATE_URL + "/{id}")
    public ResponseEntity<ExchangeRate> deleteExchangeRate(@PathVariable long id) throws URISyntaxException {
        if (exchangeRateRepository.findById(id).isEmpty()) {
        	return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    	
    	exchangeRateRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    
    @Operation(security = { @SecurityRequirement(name = "bearer-key") })
    @GetMapping(EXCHANGE_RATE_URL + "/{id}")
    public ResponseEntity<ExchangeRate> getExchangeRate(@PathVariable Long id) {
            Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findById(id);
            if (exchangeRate.isPresent()) {
                    return ResponseEntity.ok(exchangeRate.get());
            }
            return ResponseEntity.notFound().build();
    }
        
}
