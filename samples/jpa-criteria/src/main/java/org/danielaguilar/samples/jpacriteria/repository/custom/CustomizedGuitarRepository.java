package org.danielaguilar.samples.jpacriteria.repository.custom;

import org.danielaguilar.samples.jpacriteria.model.Guitar;
import org.danielaguilar.samples.jpacriteria.model.Store;

import java.util.List;

public interface CustomizedGuitarRepository {

    List<Store> convolutedStringQueryExample();

    List<Guitar> getInventoryUsingCriteria(boolean showAllStores, int salesPersonId);

    List<Guitar> getInventoryUsingJPQL(boolean showAllStores, int salesPersonId);
}
