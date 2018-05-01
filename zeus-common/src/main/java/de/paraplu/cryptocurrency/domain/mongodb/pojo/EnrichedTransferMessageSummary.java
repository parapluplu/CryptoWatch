package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class EnrichedTransferMessageSummary {

    @Id
    private String     tokenAddress;
    private BigDecimal avgAmount;
    private long       count;
    @Id
    private int        year;
    @Id
    private int        month;
    @Id
    private int        day;
    @Id
    private int        hour;
    @Id
    private int        minute;

}
