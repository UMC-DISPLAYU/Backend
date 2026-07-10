package com.example.demo.domain.lounge.domain.vo;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class UserId implements Serializable {
    private Long value;

    protected UserId(){}
    public UserId(Long value){
        if(value == null || value <=0){
            throw new IllegalArgumentException("userId must be positive");

        }
        this.value = value;
    }
    public Long value() {
        return value;
    }
}
