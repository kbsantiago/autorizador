package br.com.vr.miniautorizador.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class CardEntity {
    @Id
    private String id;
    private String cardNumber;
    private String password;
    private BigDecimal balance;
}
