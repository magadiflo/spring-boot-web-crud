package dev.magadiflo.springbootwebcrud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record RegisterAuthorDTO(String firstName,
                                String lastName,
                                @JsonFormat(pattern = "dd/MM/yyyy") LocalDate birthdate) {
}
