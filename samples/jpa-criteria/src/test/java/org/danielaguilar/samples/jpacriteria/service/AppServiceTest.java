package org.danielaguilar.samples.jpacriteria.service;

import org.danielaguilar.samples.jpacriteria.model.Guitar;
import org.danielaguilar.samples.jpacriteria.model.Store;
import org.danielaguilar.samples.jpacriteria.repository.GuitarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AppServiceTest {

    @Autowired
    private AppService service;

    @Autowired
    private GuitarRepository repo;

    @Test
    public void testConvolutedExample() {
        List<Store> store = repo.convolutedStringQueryExample();
        assertEquals(0, store.size());
    }

    @Test
    public void testFetchAllStores() {
        final int guitarCountNoMapleFretboards = 8;
        List<Guitar> guitars = service.getInventoryUsingSpecs(true, 1);
        assertEquals(guitarCountNoMapleFretboards, guitars.size());
    }

    @Test
    public void testFetchSingleStore() {
        List<Guitar> guitars = service.getInventoryUsingSpecs(false, 1);
        boolean anyFromOtherStore = guitars.stream().anyMatch(g -> g.getStore().getId() != Store.ROSEVILLE);

        assertFalse(anyFromOtherStore);
        assertEquals(5, guitars.size());
    }

    @Test
    public void testFetchNewHireAllStores() {
        List<Guitar> guitars = service.getInventoryUsingSpecs(true, AppService.DAVE);
        boolean anyPricedHigherThanCap = guitars.stream()
                .anyMatch(g -> g.getPrice().compareTo(AppService.NEW_HIRE_PRICE_CAP) > 0);

        assertFalse(anyPricedHigherThanCap);
        assertEquals(2, guitars.size());
    }

    @Test
    public void testFetchNewHire() {
        List<Guitar> guitars = service.getInventoryUsingSpecs(false, AppService.DAVE);
        boolean anyPricedHigherThanCap = guitars.stream()
                .anyMatch(g -> g.getPrice().compareTo(AppService.NEW_HIRE_PRICE_CAP) > 0);
        boolean anyFromOtherStore = guitars.stream().anyMatch(g -> g.getStore().getId() != Store.ROSEVILLE);

        assertFalse(anyPricedHigherThanCap);
        assertFalse(anyFromOtherStore);
        assertEquals(1, guitars.size());
    }

    @Test
    public void testQueryParity() {
        class RunConfig {
            boolean showAllStores;
            int salesPersonId;

            RunConfig(boolean showAllStores, int salesPersonId) {
                this.showAllStores = showAllStores;
                this.salesPersonId = salesPersonId;
            }
        }

        List<Guitar> list1;
        List<Guitar> list2;
        List<Guitar> list3;

        List<RunConfig> configurations = Arrays.asList(
                new RunConfig(true, 1),
                new RunConfig(false, 1),
                new RunConfig(true, AppService.DAVE),
                new RunConfig(false, AppService.DAVE)
        );

        for (RunConfig config : configurations) {
            list1 = service.getInventoryUsingSpecs(config.showAllStores, config.salesPersonId);
            list2 = repo.getInventoryUsingCriteria(config.showAllStores, config.salesPersonId);
            list3 = repo.getInventoryUsingJPQL(config.showAllStores, config.salesPersonId);

            assertIterableEquals(list1, list2);
            assertIterableEquals(list1, list3);
        }
    }
}
