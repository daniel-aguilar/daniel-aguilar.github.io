package org.danielaguilar.samples.jpacriteria.service;

import org.danielaguilar.samples.jpacriteria.model.Guitar;
import org.danielaguilar.samples.jpacriteria.model.Store;
import org.danielaguilar.samples.jpacriteria.repository.GuitarRepository;
import org.danielaguilar.samples.jpacriteria.repository.StoreRepository;
import org.danielaguilar.samples.jpacriteria.repository.specs.GuitarSpecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AppService {

    public static final int DAVE = 245;
    public static final BigDecimal NEW_HIRE_PRICE_CAP = BigDecimal.valueOf(100);

    @Autowired
    private StoreRepository stores;

    @Autowired
    private GuitarRepository guitars;

    public List<Guitar> getInventoryUsingSpecs(boolean showAllStores, int salesPersonId) {
        boolean isNewHire = salesPersonId == DAVE;

        Specification<Guitar> spec = GuitarSpecs.noMapleFretboards();

        if (!showAllStores) {
            Store roseville = stores.getReferenceById(Store.ROSEVILLE);
            spec = spec.and(GuitarSpecs.from(roseville));
        }

        if (isNewHire) {
            spec = spec.and(GuitarSpecs.limitPriceTo(NEW_HIRE_PRICE_CAP));
        }

        return guitars.findAll(spec);
    }
}
