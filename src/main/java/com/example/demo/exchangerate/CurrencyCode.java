package com.example.demo.exchangerate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@NoArgsConstructor
@Table(name = "currency_code", schema = "public") // needed for PostgreSQL
public class CurrencyCode implements Serializable {

    private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String currencyCode;
    
    @NotNull
    @Size(min = 1, max = 255)
    @NonNull
	private String country;
    
    @NotNull
    @Positive
    @NonNull
	private int rateQty;

    // CascadeType.ALL - enable removing the relation (exchange_rate.currency_code_id)
    // orphanRemoval - enable removing the related entity (exchange_rate)
    // fetch - lazy by default
    @OneToMany(mappedBy = "currencyCode", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore // to avoid infinite recursion
	private List<ExchangeRate> exchangeRates = new ArrayList<ExchangeRate>();

}
