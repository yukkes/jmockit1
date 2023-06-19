package petclinic.vets;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

import petclinic.util.Person;

/**
 * A veterinarian.
 */
@Entity
public class Vet extends Person {
    private static final long serialVersionUID = 1L;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(joinColumns = @JoinColumn(name = "vetId"), inverseJoinColumns = @JoinColumn(name = "specialtyId"))
    @OrderBy("name")
    private final List<Specialty> specialties = new ArrayList<>();

    public List<Specialty> getSpecialties() {
        return specialties;
    }

    public int getNrOfSpecialties() {
        return specialties.size();
    }
}
