package org.danielaguilar.samples.jpacriteria.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
public class Guitar {

    @Id
    @Column(name = "serial_number")
    private int serialNumber;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToOne
    @JoinColumn(name = "body_finish_id")
    private BodyFinish finish;

    @ManyToOne
    @JoinColumn(name = "fretboard_wood_id")
    private FretboardWood fretboardWood;

    private BigDecimal price;

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public BodyFinish getFinish() {
        return finish;
    }

    public void setFinish(BodyFinish finish) {
        this.finish = finish;
    }

    public FretboardWood getFretboardWood() {
        return fretboardWood;
    }

    public void setFretboardWood(FretboardWood fretboardWood) {
        this.fretboardWood = fretboardWood;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Guitar guitar = (Guitar) o;
        return serialNumber == guitar.serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(serialNumber);
    }
}
