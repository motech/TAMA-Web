package com.beehyv.tama.patient.repository.one;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.functors.AllPredicate;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.ViewResult;
import org.ektorp.support.ListFunction;
import org.ektorp.support.View;
import org.ektorp.support.CouchDbRepositorySupport;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.repository.AuditableCouchRepository;
import org.motechproject.tama.common.repository.AllAuditRecords;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.motechproject.tama.refdata.objectcache.AllGendersCache;
import org.motechproject.tama.refdata.objectcache.AllHIVTestReasonsCache;
import org.motechproject.tama.refdata.objectcache.AllIVRLanguagesCache;
import org.motechproject.tama.refdata.objectcache.AllModesOfTransmissionCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.motechproject.tama.common.repository.AuditableCouchRepository;

@Component
public class EditPatientIdInPatientsDoc {

	private static final Logger LOGGER = Logger
			.getLogger(EditPatientIdInPatientsDoc.class);

	@Autowired
	AllAuditRecords allAuditRecords;

	@Autowired
	EditPatientIdInUniquePatientFieldDoc allUniquePatientFieldsNew;

	@Resource(name = "tamaDbConnector")
	private CouchDbConnector tamaDbConnector;

	@Autowired
	private AllPatients allPatients;

	public void editPatientId(String patientId, String docid, String clinicId,
			String newPatientId) {
		List<Patient> patients = allPatients.findAllByPatientId(patientId);
		for (Patient patient : patients) {
			if (patient.getId().equals(docid)) {
				patient.setPatientId(newPatientId);
				updateUniqueFieldDoc(patient, "karthik@beehyv");
			}
		}

	}

	public void updateUniqueFieldDoc(Patient entity, String userName) {
		update(entity, userName);
		allUniquePatientFieldsNew.updatingNewPatientIds(entity);
	}

	public void update(Patient entity, String userName) {
		allPatients.update(entity, userName);
	}

	public Patient get(String id) {
		Patient patient = (Patient) allPatients.get(id);
		return patient;
	}

	public List<Patient> getAll() {
		return allPatients.getAll();

	}

}
