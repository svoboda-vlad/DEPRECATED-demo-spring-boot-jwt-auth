package com.example.demo.exchangerate;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Long> {
	
	List<ExchangeRate> findByCurrencyCode(CurrencyCode currencyCode);
	
	List<ExchangeRate> findByRateDate(LocalDate rateDate);	

}
