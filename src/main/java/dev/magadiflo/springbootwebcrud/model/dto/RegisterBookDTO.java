package dev.magadiflo.springbootwebcrud.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

public record RegisterBookDTO(String title,
                              @JsonFormat(pattern = "dd/MM/yyyy") LocalDate publicationDate,
                              Boolean onlineAvailability,
                              List<Long> authorIdList) {
    // Constructor compacto
    public RegisterBookDTO {
        onlineAvailability = onlineAvailability != null && onlineAvailability;
    }
}
