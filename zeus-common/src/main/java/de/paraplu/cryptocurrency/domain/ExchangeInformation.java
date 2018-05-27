package de.paraplu.cryptocurrency.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExchangeInformation {
    private boolean in;
    private boolean out;

    private String  address;
    private String  name;
}
