package org.motechproject.tama.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.View;
import org.motechproject.tama.TamaException;
import org.motechproject.tama.domain.Patient;
import org.motechproject.tama.domain.UniquePatientField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@View(name = "all", map = "function(doc) { if (doc.documentType == 'AllUniquePatientFields') { emit(null, doc) } }")
public class AllUniquePatientFields extends AbstractCouchRepository<UniquePatientField> {

    @Autowired
    public AllUniquePatientFields(@Qualifier("tamaDbConnector") CouchDbConnector db) {
        super(UniquePatientField.class, db);
        initStandardDesignDocument();
    }

    public void add(Patient patient) {
        List<String> addedFields = new ArrayList<String>();
        for (String field : patient.uniqueFields()) {
            try {
                this.add(new UniquePatientField(field));
                addedFields.add(field);
            } catch (UpdateConflictException e) {
                for (String addedField : addedFields) {
                    this.remove(this.get(addedField));
                }
                throw new TamaException("Unique constraint exception: " + field, e);
            }
        }
    }

    public void remove(Patient patient) {
        for (String field : patient.uniqueFields()) {
            this.remove(this.get(field));
        }
    }

    public List<UniquePatientField> get(Patient patient) {
        List<UniquePatientField> uniqueFields = new ArrayList<UniquePatientField>();
        for (String field : patient.uniqueFields())
            uniqueFields.add(this.get(field));
        return uniqueFields;
    }
}
