package com.beehyv.tama.edit;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class UpdateConstraintsInUniquePatientFieldDoc {

	private static final Logger LOGGER = Logger
			.getLogger(UpdateConstraintsInUniquePatientFieldDoc.class);

	@Autowired
	protected CouchDbConnector tamaDbConnector;

	@Autowired
	private AllPatients allPatients;

	@Autowired
	private AllUniquePatientFields allUniquePatientFields;

	Properties prop = new Properties();
	InputStream input = null;

	List<Patient> patients = new ArrayList<Patient>();
	
	private int count = 0;

	@Test
	public void addConstraints() throws IOException {

		count++;
		patients = allPatients.getAll();

		for (Patient patient : patients) {
			LOGGER.debug("For Patient::::" + patient.getPatientId() + "::");
			List<UniquePatientField> uniqueFields = allUniquePatientFields
					.get(patient);

			if (uniqueFields.size() == 2) {
				LOGGER.debug("Size is 2");
			}

			else if (uniqueFields.isEmpty()) {
				LOGGER.debug("Size is 0");
				if (!checkDummyPatientId(patient)) {
					allUniquePatientFields.add(new UniquePatientField(patient
							.clinicAndPatientId(), patient.getId()));
				}
				if (!checkDummyPaasscode(patient)) {
					allUniquePatientFields.add(new UniquePatientField(patient
							.phoneNumberAndPasscode(), patient.getId()));
				}
			} else if (uniqueFields.size() == 1) {
				LOGGER.debug("Size is 1");
				for (UniquePatientField uniqueField : uniqueFields) {
					if (uniqueField.getId()
							.equals(patient.clinicAndPatientId())) {
						if (!checkDummyPaasscode(patient)) {
							allUniquePatientFields.add(new UniquePatientField(
									patient.phoneNumberAndPasscode(), patient
											.getId()));
						}
					} else if (uniqueField.getId().equals(
							patient.phoneNumberAndPasscode())) {
						if (!checkDummyPatientId(patient)) {
							allUniquePatientFields.add(new UniquePatientField(
									patient.clinicAndPatientId(), patient
											.getId()));
						}
					}
				}
			}
		}
		if(totalNoOfConstraints() < 2*(patients.size()) ){
			 addConstraints();
		}

	}

	private int totalNoOfConstraints(){
		int total = 0;
		for (Patient patient : patients) {
			List<UniquePatientField> uniqueFields = allUniquePatientFields.get(patient);
			total +=uniqueFields.size();
		}
		return total;		
	}
	
	private boolean checkDummyPatientId(Patient patient) {
		for (Patient dbPatient : patients) {
			if (dbPatient.getPatientId().equals(patient.getPatientId())
					&& !dbPatient.getId().equals(patient.getId())
					&& dbPatient.getClinic_id().equals(patient.getClinic_id())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkDummyPaasscode(Patient patient) {
		for (Patient dbPatient : patients) {
			if (dbPatient.getMobilePhoneNumber().equals(
					patient.getMobilePhoneNumber())
					&& dbPatient
							.getPatientPreferences()
							.getPasscode()
							.equals(patient.getPatientPreferences()
									.getPasscode())
					&& !dbPatient.getId().equals(patient.getId())) {
				return true;
			}
		}

		return false;
	}

}
