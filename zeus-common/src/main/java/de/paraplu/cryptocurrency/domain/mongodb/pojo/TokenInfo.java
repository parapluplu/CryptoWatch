package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import org.springframework.data.annotation.Id;

public class TokenInfo {
    @Id
    private String address;
    private String symbol;
    private int    decimals;
}
