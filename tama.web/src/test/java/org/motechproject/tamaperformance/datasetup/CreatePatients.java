package org.motechproject.tamaperformance.datasetup;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.mockito.Mock;
import org.motechproject.tama.clinicvisits.domain.ClinicVisit;
import org.motechproject.tama.clinicvisits.repository.AllClinicVisits;
import org.motechproject.tama.facility.builder.ClinicBuilder;
import org.motechproject.tama.facility.builder.ClinicianBuilder;
import org.motechproject.tama.facility.domain.Clinic;
import org.motechproject.tama.facility.domain.Clinician;
import org.motechproject.tama.facility.repository.AllClinics;
import org.motechproject.tama.patient.builder.*;
import org.motechproject.tama.patient.domain.*;
import org.motechproject.tama.patient.repository.AllLabResults;
import org.motechproject.tama.patient.repository.AllVitalStatistics;
import org.motechproject.tama.refdata.domain.*;
import org.motechproject.tama.refdata.repository.*;
import org.motechproject.tama.security.AuthenticatedUser;
import org.motechproject.tama.security.LoginSuccessHandler;
import org.motechproject.tama.web.ClinicController;
import org.motechproject.tama.web.ClinicianController;
import org.motechproject.tama.web.PatientController;
import org.motechproject.tama.web.TreatmentAdviceController;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.reset;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@Component
public class CreatePatients {
    private final Logger log;

    @Autowired
    AllClinics allClinics;
    @Autowired
    AllClinicVisits allClinicVisits;
    @Autowired
    AllCities allCities;
    @Autowired
    ClinicController clinicController;
    @Autowired
    TreatmentAdviceController treatmentAdviceController;
    @Autowired
    ClinicianController clinicianController;
    @Autowired
    AllDrugs allDrugs;
    @Autowired
    AllLabResults allLabResults;
    @Autowired
    AllLabTests allLabTests;
    @Autowired
    AllVitalStatistics allVitalStatistics;
    @Autowired
    AllGenders allGenders;
    @Autowired
    AllRegimens allRegimens;
    @Autowired
    AllDosageTypes allDosageTypes;
    @Autowired
    AllMealAdviceTypes allMealAdviceTypes;
    @Autowired
    PatientController patientController;

    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;


    public CreatePatients() {
        initMocks(this);
        log = Logger.getLogger(this.getClass().getName());
    }

    private void login(Clinic clinic) {
        AuthenticatedUser user = mock(AuthenticatedUser.class);
        HttpSession session = mock(HttpSession.class);

        reset(request);
        when(user.getClinicId()).thenReturn(clinic.getId());
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(LoginSuccessHandler.LOGGED_IN_USER)).thenReturn(user);
    }

    public void createClinicians(int numberOfClinician) {
        City city = allCities.getAll().get(0);
        for (int i = 0; i < numberOfClinician; i++) {
            Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinic" + i).withCity(city).build();
            clinicController.create(clinic, bindingResult, uiModel, request);

            Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withName("clinician" + i).withUserName("clinician" + i).withClinic(clinic).build();
            clinicianController.create(clinician, bindingResult, uiModel, request);
        }
    }

    public void createPatients(LocalDate today, int numberOfPatients) {
        LocalTime doseTime = new LocalTime(10, 0);

        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        List<Clinic> clinics = allClinics.getAll();

        for (int patientsCreated = 0; patientsCreated < numberOfPatients; patientsCreated++) {
            DateTime timeOfRegistration = DateUtil.newDateTime(today, 10, 0, 0);
            doseTime = getFreeSlotTime(doseTime, patientsCreated);
            //Divide the patients among the clinics
            Clinic clinic = clinics.get(patientsCreated % clinics.size());
            login(clinic);
            Patient patient = createActivePatient(medicalHistory, clinic);
            recordFirstClinicVisit(patient, today, doseTime, timeOfRegistration);
            log.info("Created patient:" + patient.getPatientId() + ":for clinic:" + clinic.getName() + "with dose time:" + doseTime);
        }
    }

    private void recordFirstClinicVisit(Patient patient, LocalDate startDate, LocalTime doseTime, DateTime now) {
        ClinicVisit baselineClinicVisit = allClinicVisits.getBaselineVisit(patient.getId());
        TreatmentAdvice treatmentAdvice = createTreatmentAdviceForPatient(patient, doseTime, startDate);
        List<String> labResultIds = createLabResults(patient, startDate);
        VitalStatistics vitalStatistics = createVitalStatistics(patient, startDate);
        allClinicVisits.updateVisitDetails(baselineClinicVisit.getId(), now, patient.getId(), treatmentAdvice.getId(), labResultIds, vitalStatistics.getId(), null);
    }

    private LocalTime getFreeSlotTime(LocalTime doseTime, int patientsCreated) {
        // 28 Patients per slot
        if ((patientsCreated) % 28 == 0) {
            doseTime = doseTime.plusMinutes(15);
        }
        return doseTime;
    }

    private VitalStatistics createVitalStatistics(Patient patient, LocalDate startDate) {
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().withPatientId(patient.getId()).withCaptureDate(startDate).build();
        allVitalStatistics.add(vitalStatistics);
        return vitalStatistics;
    }

    private List<String> createLabResults(Patient patient, LocalDate startDate) {
        List<String> labResultIds = new ArrayList<String>();
        for (LabTest labTest : allLabTests.getAll()) {
            LabResult labResult = LabResultBuilder.startRecording().withLabTest(labTest).withPatientId(patient.getId()).withResult("100").withTestDate(startDate).build();
            allLabResults.add(labResult);
            labResultIds.add(labResult.getId());
        }
        return labResultIds;
    }

    private Patient createActivePatient(MedicalHistory medicalHistory, Clinic clinic) {
        Gender gender = allGenders.getAll().get(0);
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).withGender(gender).build();
        patientController.create(patient, bindingResult, uiModel, request);
        patientController.activate(patient.getId(), uiModel, request);
        return patient;
    }

    private TreatmentAdvice createTreatmentAdviceForPatient(Patient patient, LocalTime doseTime, LocalDate startDate) {
        Drug drug = allDrugs.getAll().get(0);
        Brand[] brands = drug.getBrands().toArray(new Brand[0]);
        DosageType dosageType = allDosageTypes.getAll().get(0);
        MealAdviceType mealAdviceType = allMealAdviceTypes.getAll().get(0);

        DrugDosage drugDosage = DrugDosageBuilder
                .startRecording()
                .withDrug(drug)
                .withMorningTime(doseTime.toString())
                .withDateRange(startDate, startDate.plusYears(2))
                .withDosageType(dosageType)
                .withMealAdviceType(mealAdviceType)
                .withBrand(brands[0])
                .build();

        Regimen regimen = allRegimens.getAll().get(0);

        DrugCompositionGroup[] drugCompositionGroups = regimen.getDrugCompositionGroups().toArray(new DrugCompositionGroup[0]);

        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder
                .startRecording()
                .withDefaults()
                .withDrugDosages(drugDosage)
                .withPatientId(patient.getId())
                .withRegimenId(regimen.getId())
                .withDrugCompositionGroupId(drugCompositionGroups[0].getId())
                .build();

        treatmentAdviceController.create(bindingResult, uiModel, treatmentAdvice);
        return treatmentAdvice;
    }
}

