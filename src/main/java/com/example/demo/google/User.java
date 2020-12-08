package com.example.demo.google;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String givenName;
    private String familyName;
    private String sub;
}
