package dev.magadiflo.springbootwebcrud.persistence.repository.specification;

import dev.magadiflo.springbootwebcrud.model.projection.IAuthorProjection;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Esta clase es similar a la clase AuthorSpecs donde implementamos
 * de otra forma los criterios de consulta.
 */

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuthorSpecification implements Specification<IAuthorProjection> {

    private String q;
    private LocalDate birthdate;

    /**
     * Crea una cláusula WHERE para una consulta de la entidad a la que se hace referencia en forma de predicado para
     * la raíz y CriteriaQuery dados.
     */
    @Override
    public Predicate toPredicate(Root<IAuthorProjection> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicatesOR = new ArrayList<>();
        List<Predicate> predicatesAND = new ArrayList<>();

        if (StringUtils.hasText(q)) {
            Predicate firstNamePredicate = criteriaBuilder.like(root.get("firstName"), "%" + q + "%");
            Predicate lastNamePredicate = criteriaBuilder.like(root.get("lastName"), "%" + q + "%");

            predicatesOR.add(firstNamePredicate);
            predicatesOR.add(lastNamePredicate);
        }

        if (birthdate != null) {
            Predicate birthdatePredicate = criteriaBuilder.equal(root.get("birthdate"), this.birthdate);

            predicatesAND.add(birthdatePredicate);
        }

        if (predicatesOR.isEmpty() && predicatesAND.isEmpty()) {
            return null;
        }

        if (!predicatesOR.isEmpty() && !predicatesAND.isEmpty()) {
            Predicate[] predicateArrayOR = predicatesOR.toArray(new Predicate[0]);
            Predicate[] predicateArrayAND = predicatesAND.toArray(new Predicate[0]);

            Predicate or = criteriaBuilder.or(predicateArrayOR);
            Predicate and = criteriaBuilder.and(predicateArrayAND);

            return criteriaBuilder.and(and, criteriaBuilder.or(or));
        }

        if (!predicatesOR.isEmpty()) {
            Predicate[] predicateArrayOR = predicatesOR.toArray(new Predicate[0]);
            return criteriaBuilder.or(predicateArrayOR);
        }

        Predicate[] predicateArrayAND = predicatesAND.toArray(new Predicate[0]);
        return criteriaBuilder.and(predicateArrayAND);
    }
}
