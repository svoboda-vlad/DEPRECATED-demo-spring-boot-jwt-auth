package com.example.demo.google;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class GoogleIdTokenEntity {
	
	@NonNull
	private String idToken;

}
