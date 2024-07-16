package org.danielaguilar.samples.jpacriteria.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
public class Store {

    public static final int ROSEVILLE = 1;
    public static final int FOLSOM = 2;

    @Id
    private int id;

    private String location;

    @OneToMany(mappedBy = "store")
    private List<Guitar> guitars;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
