package com.beehyv.tama.edit;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
public class FindUniquePatientField {

	@Autowired
    protected CouchDbConnector tamaDbConnector;
	
	@Autowired
	private AllPatients allPatients;
	
	@Autowired
	private AllUniquePatientFields allUniquePatientFields;
	
	@Test
	public void getDocs() throws IOException {
		File file = new File("/home/abhinav/Desktop/karthik/patients.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			// if file doesnt exists, then create it
		 FileWriter fw = new FileWriter(file.getAbsoluteFile());
		 BufferedWriter bw = new BufferedWriter(fw);
		List<Patient> patients = allPatients.getAll();
		StringBuilder csvdata = new StringBuilder();
		for(Patient patient : patients){
			//bw.write("\n\n\n\n");
			List<UniquePatientField> uniqueFields = allUniquePatientFields.get(patient);
			int count = 0;
			if(uniqueFields.isEmpty()){
			String toFile = String.format("%s,BOTH MISSING,%s,\n", patient.getPatientId(),patient.getClinic().getName());
			csvdata.append(toFile);
			}
			for(UniquePatientField uniqueField:uniqueFields){
				count++;
			}
			if(count == 1){
				UniquePatientField uniqueField = uniqueFields.get(0);
				if(uniqueField.getId().contains(patient.clinicAndPatientId()) && uniqueField.getId().contains(patient.getClinic_id())){
				String toFile =	String.format("%s,ONLY ONE MISSING,%s,CLINIC_ID+PATIENT_ID\n",patient.getPatientId(),patient.getClinic().getName());
				csvdata.append(toFile);
				}
				if(uniqueField.getId().contains(patient.getMobilePhoneNumber()) && uniqueField.getId().contains(patient.getPatientPreferences().getPasscode())){
					String toFile =	String.format("%s,ONLY ONE MISSING,%s,PHONE NO + PIN\n",patient.getPatientId(),patient.getClinic().getName());
					csvdata.append(toFile);
					}
					//bw.write(s1);
				}
			if(count==2){
				String toFile = String.format("%s,HAVE BOTH ,%s,\n", patient.getPatientId(),patient.getClinic().getName());
				csvdata.append(toFile);
			}
			
		}
		//bw.write(st);
		
		bw.write(csvdata.toString());
		bw.close();
	}
}
