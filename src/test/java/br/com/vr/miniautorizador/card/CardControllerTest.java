package br.com.vr.miniautorizador.card;

import br.com.vr.miniautorizador.card.exceptions.CardAlreadyExistsException;
import br.com.vr.miniautorizador.common.constants.MsgProperties;
import br.com.vr.miniautorizador.common.security.WebSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

@WebMvcTest(CardController.class)
@Import(WebSecurityConfig.class)
class CardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    private CardEntity cardEntity;
    private Card card;
    private BigDecimal balance;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        balance = BigDecimal.valueOf(500.00);
        card = new Card("6549873025634501", "1234", null);
        cardEntity = new CardEntity("1", "6549873025634501", "1234", balance);
    }

    @Test
    void shouldCreateNewCardSuccessfully() throws Exception {
        when(cardService.create(any(CardEntity.class))).thenReturn(Optional.of(cardEntity));
        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardEntity))
                        .with(httpBasic("username", "password")))
                .andExpect(status().isCreated())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    Card card = objectMapper.readValue(json, Card.class);
                    assertThat(card.cardNumber()).isEqualTo(cardEntity.getCardNumber());
                    assertThat(card.password()).isEqualTo(cardEntity.getPassword());
                });
    }

    @Test
    void shouldReturnCurrentBalanceSuccessfully() throws Exception {
        when(cardService.getBalance("6549873025634501")).thenReturn(balance);
        mockMvc.perform(get("/cartoes/{numeroCartao}", "6549873025634501")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(balance))
                        .with(httpBasic("username", "password")))
                .andExpect(status().isOk())
                .andExpect(result -> {
                    String json = result.getResponse().getContentAsString();
                    BigDecimal newBalance = objectMapper.readValue(json, BigDecimal.class);
                    assertThat(newBalance).isEqualTo(balance);
                });
    }

    @Test
    void shouldReturn422WhenCardAlreadyExists() throws Exception {
        when(cardService.create(any(CardEntity.class))).thenThrow(
                new CardAlreadyExistsException(MsgProperties.CARD_ALREADY_EXISTS));

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardEntity))
                        .with(httpBasic("username", "password")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturn422ForInvalidCardData() throws Exception {
        cardEntity = CardEntity.builder().cardNumber("invalidCardNumber").password("12").build();
        when(cardService.create(any(CardEntity.class))).thenReturn(Optional.of(cardEntity));
        when(cardService.create(any(CardEntity.class))).thenThrow(
                new CardAlreadyExistsException(MsgProperties.CARD_DOES_NOT_EXISTS));

        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardEntity))
                        .with(httpBasic("username", "password")))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void shouldReturn401WhenAuthenticationFails() throws Exception {
        mockMvc.perform(post("/cartoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(card))
                        .with(httpBasic("wrongUsername", "wrongPassword")))
                .andExpect(status().isUnauthorized());
    }
}