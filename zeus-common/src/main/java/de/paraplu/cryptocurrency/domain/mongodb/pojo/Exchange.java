package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exchange {
    @Id
    private String       name;
    private List<String> addresses;
}
