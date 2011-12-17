package org.motechproject.tama.refdata.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.domain.CouchEntity;
import org.motechproject.tamacommon.util.UUIDUtil;

import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@TypeDiscriminator("doc.documentType == 'DrugCompositionGroup'")
public class DrugCompositionGroup extends CouchEntity {
    @NotNull
    private String id = UUIDUtil.newUUID();

    @NotNull
    private String name;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<DrugComposition> drugCompositions = new HashSet<DrugComposition>();


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addComposition(DrugComposition... drugCompositions) {
        this.drugCompositions.addAll(Arrays.asList(drugCompositions));
    }

    public DrugComposition getCompositionsFor(String drugCompositionId) {
        for (DrugComposition drugComposition : drugCompositions) {
            if (drugComposition.getId().equals(drugCompositionId))
                return drugComposition;
        }
        return null;
    }

    public Set<DrugComposition> getDrugCompositions() {
        return this.drugCompositions;
    }

    public void setDrugCompositions(Set<DrugComposition> drugCompositions) {
        this.drugCompositions = drugCompositions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DrugCompositionGroup that = (DrugCompositionGroup) o;

        if (drugCompositions != null ? !drugCompositions.equals(that.drugCompositions) : that.drugCompositions != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (drugCompositions != null ? drugCompositions.hashCode() : 0);
        return result;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
