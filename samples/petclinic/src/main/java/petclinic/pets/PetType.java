package petclinic.pets;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import petclinic.util.BaseEntity;

/**
 * The type of a {@link Pet} (for example, "Dog").
 */
@Entity
public class PetType extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @NotNull
    @Size(min = 1)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
