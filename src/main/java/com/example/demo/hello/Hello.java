package com.example.demo.hello;

import lombok.Data;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Entity
public class Hello implements Serializable {
    private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue
    private long id;

    @NotNull
    @Size(min = 1, max = 255)
    private String content;

}
