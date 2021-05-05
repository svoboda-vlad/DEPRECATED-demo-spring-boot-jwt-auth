package com.example.demo.exchangerate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyCodeRepository extends JpaRepository<CurrencyCode, Long> {
	
	CurrencyCode findByCurrencyCode(String currencyCode);

}
