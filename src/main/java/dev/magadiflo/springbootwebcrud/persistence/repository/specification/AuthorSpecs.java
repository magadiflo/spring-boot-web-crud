package dev.magadiflo.springbootwebcrud.persistence.repository.specification;

import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

/**
 * Esta clase es similar a la clase AuthorSpecification, solo que aquí definimos en
 * cada método estático un criterio de consulta, que luego será usado en la clase
 * que lo llame, mientras que en la clase AuthorSpecification implementamos la interfaz
 * Specification y definimos dentro de su método toPredicate() los criterios de consulta.
 */
public class AuthorSpecs {

    public static Specification<IAuthorProjection> fullNameContainsTheSearchedTerm(String q) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(q)) return null;
            return criteriaBuilder.or(
                    criteriaBuilder.like(root.get("firstName"), "%" + q + "%"),
                    criteriaBuilder.like(root.get("lastName"), "%" + q + "%")
            );
        };
    }

    public static Specification<IAuthorProjection> isEqualToBirthdate(LocalDate birthdate) {
        return (root, query, criteriaBuilder) -> {
            if (birthdate == null) return null;
            return criteriaBuilder.equal(root.get("birthdate"), birthdate);
        };
    }
}
