package dev.magadiflo.springbootwebcrud.model.projection;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface IAuthorProjection {
    Long getId();

    String getFirstName();

    String getLastName();

    String getFullName();

    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate getBirthdate();
}
