package org.danielaguilar.samples.jpacriteria.repository.specs;

import org.danielaguilar.samples.jpacriteria.model.*;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.math.BigDecimal;

public class GuitarSpecs {

    public static Specification<Guitar> noMapleFretboards() {
        return (root, query, builder) -> {
            Join<Guitar, FretboardWood> fretboardWood = root.join(Guitar_.FRETBOARD_WOOD);
            return builder.notEqual(fretboardWood.get(FretboardWood_.ID), FretboardWood.MAPLE);
        };
    }

    public static Specification<Guitar> from(Store store) {
        return (root, query, builder) ->
                builder.equal(root.get(Guitar_.STORE), store);
    }

    public static Specification<Guitar> limitPriceTo(BigDecimal value) {
        return (root, query, builder) ->
                builder.lessThanOrEqualTo(root.get(Guitar_.PRICE), value);
    }
}
