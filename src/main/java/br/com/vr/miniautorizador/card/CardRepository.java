package br.com.vr.miniautorizador.card;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends MongoRepository<CardEntity, String> {
    Optional<CardEntity> findByCardNumber(String cardNumber);
}
