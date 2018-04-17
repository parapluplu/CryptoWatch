package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.io.Serializable;
import java.math.BigInteger;

import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class TokenInfo implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    @Id
    @Setter(AccessLevel.NONE)
    @NonNull
    private String            address;
    private String            symbol;
    private BigInteger        decimals;

    public TokenInfo(String address, String symbol, BigInteger decimals) {
        super();
        this.address = address;
        this.symbol = symbol;
        this.decimals = decimals;
    }
}
