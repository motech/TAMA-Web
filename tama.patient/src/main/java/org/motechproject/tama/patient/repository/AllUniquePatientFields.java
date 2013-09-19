package org.motechproject.tama.patient.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.UpdateConflictException;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.tama.common.TamaException;
import org.motechproject.tama.common.repository.AbstractCouchRepository;
import org.motechproject.tama.common.util.StringUtil;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Component
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

    public void update(Patient patient, Patient dbPatient) {
        List<UniquePatientField> oldUniquePatientFields = get(patient);
        remove(patient, dbPatient);
        try {
            updateUniqueConstraintIfEdited(oldUniquePatientFields, patient, dbPatient);
        } catch (TamaException e) {
            for (UniquePatientField uniquePatientField : oldUniquePatientFields) {
                add(new UniquePatientField(uniquePatientField.getId(), uniquePatientField.getPrimaryDocId()));
            }
            throw e;
        }
    }

    public void remove(Patient patient, Patient dbPatient) {
        for (UniquePatientField field : get(patient)) {
            if (field.getId().contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)) {
                if (!patient.getMobilePhoneNumber().equals(dbPatient.getMobilePhoneNumber()) || !patient.getPatientPreferences().getPasscode().equals(dbPatient.getPatientPreferences().getPasscode())) {
                    this.remove(field);
                }
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

    public void updateUniqueConstraintIfEdited(List<UniquePatientField> oldUniquePatientFields, Patient patient, Patient dbPatient) {
        for (String field : patient.uniqueFields()) {
            try {
                if (field.contains(Patient.PHONE_NUMBER_AND_PASSCODE_UNIQUE_CONSTRAINT)) {
                    List<String> addedFields = new ArrayList<String>();
                    if (canUpdateUniqueFields(patient,dbPatient)) {
                        this.add(new UniquePatientField(field, patient.getId()));
                        addedFields.add(field);
                    }
                }
            } catch (TamaException e) {
                for (UniquePatientField uniquePatientField : oldUniquePatientFields) {
                    add(new UniquePatientField(uniquePatientField.getId(), uniquePatientField.getPrimaryDocId()));
                }
                throw e;
            }
        }
    }

    private boolean canUpdateUniqueFields(Patient patient, Patient dbPatient){
        boolean canUpdateUniqueFields = false;
        if (!patient.getMobilePhoneNumber().equals(dbPatient.getMobilePhoneNumber()) || !patient.getPatientPreferences().getPasscode().equals(dbPatient.getPatientPreferences().getPasscode())) {

            canUpdateUniqueFields =  true;
        }
        return  canUpdateUniqueFields;
    }
}
