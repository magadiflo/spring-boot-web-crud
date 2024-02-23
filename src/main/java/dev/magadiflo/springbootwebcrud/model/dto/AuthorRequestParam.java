package dev.magadiflo.springbootwebcrud.model.dto;

import java.time.LocalDate;

public record AuthorRequestParam(String q,
                                 LocalDate birthdate) {
}
