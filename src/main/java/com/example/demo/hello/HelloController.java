package com.example.demo.hello;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Transactional
@RestController
@RequiredArgsConstructor
public class HelloController {
	
    private final HelloRepository helloRepository;

    @GetMapping("/hello")
    public List<Hello> getAllHellos() {
        return helloRepository.findAll();
    }
	
    @PostMapping("/hello")
    public Hello createHello(@Valid @RequestBody Hello hello) {
        return helloRepository.save(hello);	
    }

}
