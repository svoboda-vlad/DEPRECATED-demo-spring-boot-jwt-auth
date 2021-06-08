package com.example.demo.google;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
public class GoogleIdTokenEntity {
	
	@NotNull
	@Size(min = 1, max = 2048)
	@NonNull
	private String idToken;

}
