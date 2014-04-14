package com.beehyv.tama.edit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.tama.patient.domain.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.beehyv.tama.ivr.repository.AllCallLogsNew;
import com.beehyv.tama.patient.repository.one.AllPatientsNew;
import com.beehyv.tama.patient.repository.one.AllUniquePatientFieldsNew;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/spring/applicationContext.xml"})
public class EditPatientIdTest {

	@Autowired
	AllCallLogsNew allCallLogsNew;
	@Autowired
	AllPatientsNew allPatientsNew;
	@Autowired
	AllUniquePatientFieldsNew allUniquePatientFieldsNew;
	@Autowired
    protected CouchDbConnector tamaDbConnector;
	
	static  String DATABASE_URL;
	static  String USERNAME;
	static  String PASSWORD;
	
	Properties prop = new Properties();
	InputStream input = null;
	
	private DateTime fromDate = new DateTime(2002, 07, 12, 21, 31, 15);
    
	private DateTime toDate = new DateTime().now();
	
	private CallLogSearch callLogSearch ;
	
	private static final String DELIMITER = ",";
	private static final String VALID_FILE_CONTENT_LINE_FORMAT = String.format("^[a-zA-Z0-9_-]+\\s*%s\\s*[a-zA-Z0-9_-]+$", DELIMITER);

	
	
	@Test
	public void edit() throws IOException{
		input = new FileInputStream("config.properties");
		prop.load(input);
		System.out.println(prop.getProperty("pathCSV"));
		DATABASE_URL=prop.getProperty("DATABASE_URL");
		USERNAME=prop.getProperty("USERNAME");
		PASSWORD=prop.getProperty("PASSWORD");
		String path = prop.getProperty("pathCSV");
		migrate(path);
	}
	
	
	 public void migrate(String filePath) {
	        File migrationFile = FileUtils.getFile(filePath);
	        
	        try{
	            List<String> fileContents = FileUtils.readLines(migrationFile);
	           // validateContents(fileContents);
	            for (String row : fileContents) {
	                String[] rowContents = StringUtils.tokenizeToStringArray(row, DELIMITER);
	                String oldPatientId = rowContents[0];
	                System.out.println(oldPatientId);
	                String clinicId = rowContents[1];
	                System.out.println(clinicId);
	                String newPatientId = rowContents[2];
	                System.out.println(newPatientId);
	                test(oldPatientId, clinicId,newPatientId);
	                System.out.println("dude");
	            }
	        }
	        catch(IOException e) {
	            System.out.printf("Error while processing file %s", filePath);
	        }      
	        
	 }
	 
	 
	 public void test(String oldPatientId,String clinicId,String newPatientId) throws IOException
		{
		 if(checkPatient(oldPatientId)){
			 System.out.println("Before call Log doc edit");
			 editPatientsIdInCallLogDoc(oldPatientId,clinicId,newPatientId);
			 System.out.println("after call of calllogedit");
			 editPatientsIdInPatientDoc(oldPatientId,clinicId,newPatientId);
			 System.out.println("after patient doc");
		 }
		 else
		  {
			  System.out.printf("the patient %s is not in the Couch db.",oldPatientId);
		  }
		 editPatientIdInPsql(oldPatientId, clinicId, newPatientId);
		 System.out.println("after psql");
		}
	
	public void editPatientsIdInPatientDoc(String oldPatientId,String clinicId,String newPatientId){ 
		allPatientsNew.editPatientId(oldPatientId, clinicId, newPatientId);
	}
	
	
	public void editPatientsIdInCallLogDoc(String oldPatientId,String clinicId,String newPatientId){    
		callLogSearch = new CallLogSearch(fromDate, toDate, CallLog.CallLogType.Answered, oldPatientId, true, clinicId);
			allCallLogsNew.editPatientId(callLogSearch, newPatientId);
			System.out.println("done with updating allCallLogs ANSWERED");
			callLogSearch = new CallLogSearch(fromDate, toDate, CallLog.CallLogType.Missed, oldPatientId, true, clinicId);
			allCallLogsNew.editPatientId(callLogSearch, newPatientId);
			System.out.println("done with updating allCallLogs MISSED");
	}
	
	public boolean checkPatient(String oldPatientId){
		List<Patient> patients = allPatientsNew.getAll();
		for(Patient patient:patients){
			if(patient.getPatientId().equals(oldPatientId)){
				return true;
			}
		}
		return false;
	}
	public void editPatientIdInPsql(String oldPatientId,String clinicId,String newPatientId) throws IOException
	{
		
		
	       Connection c = null;
	       Statement stmt = null;
	       Statement stmt1 = null;
	       try {
	       Class.forName("org.postgresql.Driver");
	         c = DriverManager.getConnection(DATABASE_URL,USERNAME, PASSWORD);
	         c.setAutoCommit(false);
	         System.out.println("Opened database successfully");

	         stmt = c.createStatement();
	         stmt1 = c.createStatement();
	         String sql = "UPDATE tama_reports.patient set patient_id ='"+newPatientId+"'where patient_id='"+oldPatientId+"'and clinic_id='"+clinicId+"';";
	         String sql1 = "UPDATE tama_reports.medical_history set patient_id ='"+newPatientId+"'where patient_id='"+oldPatientId+"';";
	         stmt.executeUpdate(sql);
	         stmt1.executeUpdate(sql1);
	         c.commit();

	         stmt.close();
	         stmt1.close();
	         c.close();
	       } catch ( Exception e ) {
	         System.err.println( e.getClass().getName()+": "+ e.getMessage() );
	         System.exit(0);
	       }
	       System.out.println("Operation On Psql done successfully");
	     }
	
}
