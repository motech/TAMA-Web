package com.beehyv.tama.patient.repository;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EditPatientIdInPatientsDoc {

	private static final Logger LOGGER = Logger
			.getLogger(EditPatientIdInPatientsDoc.class);


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
