package com.beehyv.tama.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.domain.UniquePatientField;
import org.motechproject.tama.patient.repository.AllPatients;
import org.motechproject.tama.patient.repository.AllUniquePatientFields;

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

	@Test
	public void addConstraints() throws IOException {

		patients = allPatients.getAll();
		StringBuilder csvdata = new StringBuilder();

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

	}

	public boolean checkDummyPatientId(Patient patient) {
		for (Patient dbPatient : patients) {
			if (dbPatient.getPatientId().equals(patient.getPatientId())
					&& !dbPatient.getId().equals(patient.getId())
					&& dbPatient.getClinic_id().equals(patient.getClinic_id())) {
				return true;
			}
		}
		return false;
	}

	public boolean checkDummyPaasscode(Patient patient) {
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
