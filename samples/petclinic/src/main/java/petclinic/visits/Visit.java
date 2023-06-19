package petclinic.visits;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import petclinic.pets.Pet;
import petclinic.util.BaseEntity;

/**
 * A visit from a pet and its owner to the clinic.
 */
@Entity
public class Visit extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @NotNull
    private String description;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "petId")
    private Pet pet;

    /**
     * Creates a new instance of Visit for the current date.
     */
    public Visit() {
        date = new Date();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }
}
