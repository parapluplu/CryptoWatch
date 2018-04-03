package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.math.BigInteger;

import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class TokenInfo {
    @Id
    @Setter(AccessLevel.NONE)
    private String     address;
    private String     symbol;
    private BigInteger decimals;

    public TokenInfo(String address, String symbol, BigInteger decimals) {
        super();
        this.address = address;
        this.symbol = symbol;
        this.decimals = decimals;
    }
}
