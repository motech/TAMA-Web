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
import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.tama.ivr.domain.CallLog;
import org.motechproject.tama.ivr.domain.CallLogSearch;
import org.motechproject.tama.patient.domain.Patient;
import org.motechproject.tama.patient.repository.AllPatients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.StringUtils;

import com.beehyv.tama.ivr.repository.EditPatientIdInCallLogsDoc;
import com.beehyv.tama.patient.repository.one.EditPatientIdInPatientsDoc;
import com.beehyv.tama.patient.repository.one.EditPatientIdInUniquePatientFieldDoc;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/applicationContext.xml" })
public class EditPatientIdInDataBase {

	private static final Logger LOGGER = Logger
			.getLogger(EditPatientIdInDataBase.class);

	@Autowired
	EditPatientIdInCallLogsDoc allCallLogsNew;
	@Autowired
	EditPatientIdInPatientsDoc allPatientsNew;
	@Autowired
	AllPatients allPatients;
	@Autowired
	EditPatientIdInUniquePatientFieldDoc allUniquePatientFieldsNew;
	@Autowired
	protected CouchDbConnector tamaDbConnector;

	static String DATABASE_URL;
	static String USERNAME;
	static String PASSWORD;

	Properties prop = new Properties();
	InputStream input = null;

	private DateTime fromDate = new DateTime(2002, 07, 12, 21, 31, 15);

	private DateTime toDate = new DateTime().now();

	private static final String DELIMITER = ",";

	@Test
	public void correctErrorneousPatientids() throws IOException {
		input = new FileInputStream("config.properties");
		prop.load(input);
		DATABASE_URL = prop.getProperty("DATABASE_URL");
		USERNAME = prop.getProperty("USERNAME");
		PASSWORD = prop.getProperty("PASSWORD");
		String path = prop.getProperty("pathCSV");
		readFromCSVAndMigrate(path);
	}

	public void readFromCSVAndMigrate(String filePath) {
		File migrationFile = FileUtils.getFile(filePath);
		// List<Patient> patients = allPatientsNew.getAll();

		try {
			List<String> fileContents = FileUtils.readLines(migrationFile);
			for (String row : fileContents) {
				String[] rowContents = StringUtils.tokenizeToStringArray(row,
						DELIMITER, false, false);
				String oldPatientId = rowContents[0];
				String docid = rowContents[1];
				String clinicId = rowContents[2];
				String newPatientId = rowContents[3];
				boolean hasPatientId = false;
				List<Patient> patients = allPatients
						.findAllByPatientId(oldPatientId);
				for (Patient patient : patients) {
					if (patient.getId().equals(docid)) {
						migrate(oldPatientId, docid, clinicId, newPatientId);
						hasPatientId = true;
					}
				}
				if (hasPatientId == false)
					LOGGER.info(oldPatientId
							+ "Patient Id is not in the couch Db .");
			}
			editPatientIdInPsql(fileContents);
		}

		catch (IOException e) {
			LOGGER.error("Error while processing file given." + filePath);
		}

	}

	public void migrate(String oldPatientId, String docid, String clinicId,
			String newPatientId) throws IOException {
		editPatientsIdInCallLogDoc(oldPatientId, docid, clinicId, newPatientId);
		editPatientsIdInPatientDoc(oldPatientId, docid, clinicId, newPatientId);
	}

	public void editPatientsIdInPatientDoc(String oldPatientId, String docid,
			String clinicId, String newPatientId) {
		allPatientsNew.editPatientId(oldPatientId, docid, clinicId,
				newPatientId);
	}

	public void editPatientsIdInCallLogDoc(String oldPatientId, String docid,
			String clinicId, String newPatientId) {
		CallLogSearch callLogSearchForAnswered = new CallLogSearch(fromDate,
				toDate, CallLog.CallLogType.Answered,
				oldPatientId.toLowerCase(), true, clinicId);
		CallLogSearch callLogSearchForMissed = new CallLogSearch(fromDate,
				toDate, CallLog.CallLogType.Missed, oldPatientId.toLowerCase(),
				true, clinicId);
		List<CallLog> callLogs = allCallLogsNew
				.findCallLogsForDateRangePatientIdAndClinic(callLogSearchForAnswered);
		callLogs.addAll(allCallLogsNew
				.findCallLogsForDateRangePatientIdAndClinic(callLogSearchForMissed));
		allCallLogsNew.editPatientId(callLogs, docid, oldPatientId,
				newPatientId);
	}

	public void editPatientIdInPsql(List<String> fileContents) {
		Connection c = null;
		Statement stmt = null;
		Statement stmt1 = null;
		try {
			Class.forName("org.postgresql.Driver");
			c = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
			c.setAutoCommit(false);
			for (String row : fileContents) {
				String[] rowContents = StringUtils.tokenizeToStringArray(row,
						DELIMITER, false, false);
				String oldPatientId = rowContents[0];
				String docid = rowContents[1];
				String clinicId = rowContents[2];
				String newPatientId = rowContents[3];
				stmt = c.createStatement();
				stmt1 = c.createStatement();
				String sql = "UPDATE tama_reports.patient set patient_id ='"
						+ newPatientId + "'where patient_id='" + oldPatientId
						+ "'and clinic_id='" + clinicId
						+ "'and patient_document_id='" + docid + "';";
				String sql1 = "UPDATE tama_reports.medical_history set patient_id ='"
						+ newPatientId
						+ "'where patient_id='"
						+ oldPatientId
						+ "'and patient_document_id='" + docid + "';";
				stmt.executeUpdate(sql);
				stmt1.executeUpdate(sql1);
				c.commit();
				stmt.close();
				stmt1.close();
			}
			c.close();
		} catch (Exception e) {
			LOGGER.error(e.getClass().getName() + ": " + e.getMessage());
			System.exit(0);
		}

	}

}
