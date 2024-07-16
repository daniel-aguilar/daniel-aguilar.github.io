package org.danielaguilar.samples.jpacriteria.repository.custom;

import org.danielaguilar.samples.jpacriteria.model.*;
import org.danielaguilar.samples.jpacriteria.service.AppService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomizedGuitarRepositoryImpl implements CustomizedGuitarRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Store> convolutedStringQueryExample() {
        boolean someCondition = true;
        Store folsomStore = em.find(Store.class, Store.FOLSOM);
        FretboardWood maple = em.find(FretboardWood.class, FretboardWood.MAPLE);
        FretboardWood ebony = em.find(FretboardWood.class, FretboardWood.EBONY);

        // Long and unformatted query...
        String jpql = "FROM Store s JOIN s.guitars g WHERE g.price > " +
                "(SELECT max(g2.price) FROM Guitar g2 JOIN g2.store s2 WHERE s2 = :folsomStore) ";
        if (someCondition) {
            // ...split across different conditions
            jpql += "AND g.fretboardWood NOT IN (:maple, :ebony) ";
        }
        jpql += "ORDER BY s.location ASC";

        Query query = em.createQuery(jpql);
        query.setParameter("folsomStore", folsomStore);
        query.setParameter("maple", maple);
        query.setParameter("ebony", ebony);
        return query.getResultList(); // No type safety
    }

    @Override
    public List<Guitar> getInventoryUsingCriteria(boolean showAllStores, int salesPersonId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Guitar> cq = cb.createQuery(Guitar.class);
        Root<Guitar> guitar = cq.from(Guitar.class);
        boolean isNewHire = salesPersonId == AppService.DAVE;

        Predicate noMapleFretboards = cb.notEqual(guitar.get(Guitar_.FRETBOARD_WOOD).get(FretboardWood_.ID),
                FretboardWood.MAPLE);
        Predicate fromRosevilleStore = cb.equal(guitar.get(Guitar_.STORE).get(Store_.ID), Store.ROSEVILLE);
        Predicate atNewHirePriceCap = cb.lessThanOrEqualTo(guitar.get(Guitar_.PRICE), AppService.NEW_HIRE_PRICE_CAP);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(noMapleFretboards);

        if (!showAllStores) {
            predicates.add(fromRosevilleStore);
        }

        if (isNewHire) {
            predicates.add(atNewHirePriceCap);
        }

        cq = cq.select(guitar).where(toVarargs(predicates));
        TypedQuery<Guitar> query = em.createQuery(cq);
        return query.getResultList();
    }

    @Override
    public List<Guitar> getInventoryUsingJPQL(boolean showAllStores, int salesPersonId) {
        boolean isNewHire = salesPersonId == AppService.DAVE;
        HashMap<String, Object> params = new HashMap<>();

		/*
		 * I manually format the string here, but I've witnessed incredibly unreadable
		 * one-liners. Plus there's a lack of tooling to standardize this.
		 */
        String jpql =
                "SELECT g " + // We have to deliberately add spacing
                "FROM Guitar g " +
                "JOIN g.fretboardWood f " +
                "JOIN g.store s " + // Premature JOIN clause (what if showAllStores is true?)
                "WHERE ";

        jpql += "f.id != :maple AND ";

        if (!showAllStores) {
            jpql += "s.id = :roseville AND ";
            params.put("roseville", Store.ROSEVILLE);
        }

        if (isNewHire) {
            jpql += "g.price <= :priceCap AND ";
            params.put("priceCap", AppService.NEW_HIRE_PRICE_CAP);
        }

        jpql += "1 = 1"; // Make sure the query does not end with AND
        Query query = em.createQuery(jpql);
        query.setParameter("maple", FretboardWood.MAPLE);
        params.keySet().forEach(k -> {
            query.setParameter(k, params.get(k));
        });

        return query.getResultList(); // No type safety
    }

    private Predicate[] toVarargs(List<Predicate> predicates) {
        return predicates.toArray(new Predicate[predicates.size()]);
    }
}
