package com.beehyv.tama.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

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
@ContextConfiguration(locations = {"/spring/applicationContext.xml"})
public class UpdateConstraintsInUniquePatientFieldDoc {

	@Autowired
    protected CouchDbConnector tamaDbConnector;
	
	@Autowired
	private AllPatients allPatients;
	
	@Autowired
	private AllUniquePatientFields allUniquePatientFields;
	
	Properties prop = new Properties();
	InputStream input = null;
	
	@Test
	public void addConstraints()throws IOException{
		input = new FileInputStream("config.properties");
		prop.load(input);
		
		File file = new File(prop.getProperty("filePath"));
		if (!file.exists()) {
			file.createNewFile();
		}
	 FileWriter fw = new FileWriter(file.getAbsoluteFile());
	 BufferedWriter bw = new BufferedWriter(fw);
		
		
		List<Patient> patients = allPatients.getAll();
		StringBuilder csvdata = new StringBuilder();

		for(Patient patient : patients){
			
			
			List<UniquePatientField> uniqueFields = allUniquePatientFields.get(patient);
			if(uniqueFields.isEmpty()){
				String toFile = String.format("%s,BOTH MISSING,%s,\n", patient.getPatientId(),patient.getClinic().getName());
				csvdata.append(toFile);
				if(checkDummyPatientId(patient)){
					bw.write("--->>>THIS IS DUMY");
				}
				else{
				for (String field : patient.uniqueFields()) {
			             allUniquePatientFields.add(new UniquePatientField(field, patient.getId()));
			            }}
			}
			if(uniqueFields.size() == 2){
				bw.write("--->>>have both.");
			}
			if(uniqueFields.size() == 1){
				for(UniquePatientField uniqueField:uniqueFields){
					if(uniqueField.getId().equals(patient.clinicAndPatientId())){
						bw.write("--->>>have ClinicId/PatientId.");
						if(checkDummyPaasscode(patient)){
							bw.write("--->>>THIS HAS DUMY PASSCODE & PHONENUMBER.");
						}
						else
						{
						allUniquePatientFields.add(new UniquePatientField(patient.phoneNumberAndPasscode(), patient.getId()));
						}  
					}
					if(uniqueField.getId().equals(patient.phoneNumberAndPasscode())){
						bw.write("--->>>have Phone no/Passcode.");
			            
						allUniquePatientFields.add(new UniquePatientField(patient.clinicAndPatientId(), patient.getId()));

					}
				}
				bw.write("--->>>have Only one.");
			}
		}
		
		bw.close();
	}
	
	public boolean checkDummyPatientId(Patient patient){
		
		List<Patient> patients = allPatients.getAll();
		for(Patient dbPatient:patients){
			if(dbPatient.getPatientId().equals(patient.getPatientId()) && !dbPatient.getId().equals(patient.getId()) && dbPatient.getClinic_id().equals(patient.getClinic_id())){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkDummyPaasscode(Patient patient){
		List<Patient> patients = allPatients.getAll();
		for(Patient dbPatient:patients){
			if(dbPatient.getMobilePhoneNumber().equals(patient.getMobilePhoneNumber()) && 
					dbPatient.getPatientPreferences().getPasscode().equals(patient.getPatientPreferences().getPasscode())
					&& !dbPatient.getId().equals(patient.getId())){
				return true;
			}
		}
		
		return false;
	}
	
}
