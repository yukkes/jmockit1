package petclinic.vets;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

import petclinic.util.BaseEntity;

/**
 * A {@linkplain Vet Vet's} specialty (for example, "Dentistry").
 */
@Entity
public class Specialty extends BaseEntity {
    @NotNull
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
