package com.example.demo.exchangerate;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "exchange_rate", schema = "public") // needed for PostgreSQL
public class ExchangeRate implements Serializable {

    private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotNull
    @NonNull
    private LocalDate rateDate;
    
    @NotNull
    @Positive
    @NonNull
    private BigDecimal rate;
    
    @NotNull
    // lazy fetching disabled in order to get currency code details
    // @ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "currency_code_id")
    @NonNull
    private CurrencyCode currencyCode;
	
}
