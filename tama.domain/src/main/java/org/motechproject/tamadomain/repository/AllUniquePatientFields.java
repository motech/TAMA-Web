package org.motechproject.tamadomain.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tamacommon.TamaException;
import org.motechproject.tamacommon.repository.AbstractCouchRepository;
import org.motechproject.tamadomain.domain.Patient;
import org.motechproject.tamadomain.domain.UniquePatientField;
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
                this.add(new UniquePatientField(field, patient.getId()));
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
        for (UniquePatientField field : get(patient)) {
            this.remove(field);
        }
    }

    public List<UniquePatientField> get(Patient patient) {
        List<UniquePatientField> uniqueFields = new ArrayList<UniquePatientField>();
        for (UniquePatientField field : findByPatientDocId(patient.getId()))
            uniqueFields.add(field);
        return uniqueFields;
    }

    @View(name = "find_by_primary_doc_id", map = "function(doc) {if (doc.documentType =='UniquePatientField' && doc.primaryDocId) {emit(doc.primaryDocId, doc._id);}}")
    public List<UniquePatientField> findByPatientDocId(String patientDocId) {
        ViewQuery q = createQuery("find_by_primary_doc_id").key(patientDocId).includeDocs(true);
        return db.queryView(q, UniquePatientField.class);
    }

}
