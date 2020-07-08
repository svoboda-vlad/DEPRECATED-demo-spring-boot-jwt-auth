package com.example.demo.hello;

import org.springframework.data.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HelloRepository extends JpaRepository<Hello, Long> {

}
