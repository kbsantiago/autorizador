package br.com.vr.miniautorizador.card;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);
    CardEntity convert(Card card);
    @Mapping(source = "cardNumber", target = "cardNumber")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "balance", target = "balance", ignore = true)
    Card convertWithoutBalance(CardEntity cardEntity);
    @Mapping(source = "cardNumber", target = "cardNumber")
    @Mapping(source = "password", target = "password")
    @Mapping(source = "balance", target = "balance")
    Card convert(CardEntity cardEntity);
}
