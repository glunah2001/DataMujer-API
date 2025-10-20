package com.UNED.APIDataMujer.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "profileType"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = PhysicalPersonDTO.class, name = "physical"),
        @JsonSubTypes.Type(value = LegalPersonDTO.class, name = "legal")
})
public sealed interface ProfileDTO permits PhysicalPersonDTO, LegalPersonDTO{ }
