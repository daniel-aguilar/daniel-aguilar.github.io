package org.danielaguilar.samples.jpacriteria.repository;

import org.danielaguilar.samples.jpacriteria.model.Guitar;
import org.danielaguilar.samples.jpacriteria.repository.custom.CustomizedGuitarRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GuitarRepository extends JpaRepository<Guitar, Integer>,
        JpaSpecificationExecutor<Guitar>, CustomizedGuitarRepository {

}
