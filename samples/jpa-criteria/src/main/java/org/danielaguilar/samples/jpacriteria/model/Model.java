package org.danielaguilar.samples.jpacriteria.model;

import javax.persistence.*;

@Entity
public class Model {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;

    private String name;

    @Column(name = "string_count")
    private int stringCount;

    @Column(name = "is_hollow_body")
    private boolean isHollowBody;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStringCount() {
        return stringCount;
    }

    public void setStringCount(int stringCount) {
        this.stringCount = stringCount;
    }

    public boolean isHollowBody() {
        return isHollowBody;
    }

    public void setHollowBody(boolean hollowBody) {
        isHollowBody = hollowBody;
    }
}
