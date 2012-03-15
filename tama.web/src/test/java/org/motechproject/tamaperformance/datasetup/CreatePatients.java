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
import org.motechproject.tama.patient.service.PatientService;
import org.motechproject.tama.patient.service.TreatmentAdviceService;
import org.motechproject.tama.refdata.domain.*;
import org.motechproject.tama.refdata.repository.*;
import org.motechproject.tama.web.ClinicController;
import org.motechproject.tama.web.ClinicianController;
import org.motechproject.tama.web.PatientController;
import org.motechproject.tamadatasetup.service.TAMADateTimeService;
import org.motechproject.tamaperformance.TestConfig;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.MockitoAnnotations.initMocks;

@Component
public class CreatePatients {

    @Autowired
    private PatientService patientService;
    @Autowired
    private AllClinics allClinics;
    @Autowired
    private AllClinicVisits allClinicVisits;
    @Autowired
    private TreatmentAdviceService treatmentAdviceService;
    @Autowired
    private AllDrugs allDrugs;
    @Autowired
    private AllLabResults allLabResults;
    @Autowired
    private AllLabTests allLabTests;
    @Autowired
    private AllVitalStatistics allVitalStatistics;
    @Autowired
    private AllGenders allGenders;
    @Autowired
    AllRegimens allRegimens;
    @Autowired
    AllDosageTypes allDosageTypes;
    @Autowired
    AllMealAdviceTypes allMealAdviceTypes;
    @Autowired
    PatientController patientController;
    @Autowired
    ClinicController clinicController;
    @Autowired
    ClinicianController clinicianController;

    @Mock
    private BindingResult bindingResult;
    @Mock
    private Model uiModel;
    @Mock
    private HttpServletRequest request;

    private List<Drug> drugs;
    private List<Clinic> allClinicsData;
    private List<LabTest> labTests;
    private LocalDate today;
    private List<Gender> genders;
    private List<Regimen> regimens;
    private List<DosageType> dosageTypes;
    private List<MealAdviceType> mealAdviceTypes;
    private DateTime now;

    public CreatePatients() {
    }

    public void setup() {
        initMocks(this);
        drugs = allDrugs.getAll();
        allClinicsData = allClinics.getAll();
        labTests = allLabTests.getAll();
        genders = allGenders.getAll();
        regimens = allRegimens.getAll();
        dosageTypes = allDosageTypes.getAll();
        mealAdviceTypes = allMealAdviceTypes.getAll();
    }

    public void createClinicians(int numberOfClinician) {
        setup();
        for (int i = 0; i < numberOfClinician; i++) {
            Clinic clinic = ClinicBuilder.startRecording().withDefaults().withName("clinic" + i).build();
            Clinician clinician = ClinicianBuilder.startRecording().withDefaults().withName("clinician" + i).withUserName("clinician" + i).build();

            clinicController.create(clinic, bindingResult, uiModel, request);
            clinicianController.create(clinician, bindingResult, uiModel, request);
        }
    }

    public void createPatients(TAMADateTimeService tamaDateTimeService, LocalDate startDate, LocalDate endDate) {
        setup();
        int noOfPatients = 0;
        LocalTime doseTime = new LocalTime(10, 0);

        tamaDateTimeService.adjustDateTime(DateUtil.newDateTime(startDate));
        MedicalHistory medicalHistory = MedicalHistoryBuilder.startRecording().withDefaults().build();
        while (DateUtil.isOnOrBefore(startDate, endDate)) {
            today = startDate;
            now = DateUtil.newDateTime(today, 10, 0, 0);
            for (int i = 0; i < TestConfig.numOfClinics; i++) {
                for (int j = 0; j < TestConfig.numPatientsPerDay; j++) {
                    if ((noOfPatients) % 30 > 28) {
                        doseTime = doseTime.plusMinutes(15);
                    }
                    Clinic clinic = allClinicsData.get(i);
                    Patient patient = createPatient(medicalHistory, clinic);
                    ClinicVisit baselineClinicVisit = activatePatientForFirstTime(patient);
                    TreatmentAdvice treatmentAdvice = createTreatmentAdviceForPatient(patient, doseTime);
                    List<String> labResultIds = createLabResults(patient);
                    VitalStatistics vitalStatistics = createVitalStatistics(patient);
                    allClinicVisits.updateVisitDetails(baselineClinicVisit.getId(), now, patient.getId(), treatmentAdvice.getId(), labResultIds, vitalStatistics.getId(), null);

                    noOfPatients++;
                    System.out.println("Created patient:" + patient.getPatientId() + ":for clinic:" + clinic.getName() + "with dose time:" + doseTime);
                }
            }
            startDate = startDate.plusDays(1);
            tamaDateTimeService.adjustDateTime(DateUtil.newDateTime(startDate));
        }
    }

    private VitalStatistics createVitalStatistics(Patient patient) {
        VitalStatistics vitalStatistics = VitalStatisticsBuilder.startRecording().withDefaults().withPatientId(patient.getId()).withCaptureDate(today).build();
        allVitalStatistics.add(vitalStatistics);
        return vitalStatistics;
    }

    private List<String> createLabResults(Patient patient) {
        List<String> labResultIds = new ArrayList<String>();
        for (LabTest labTest : labTests) {
            LabResult labResult = LabResultBuilder.startRecording().withLabTest(labTest).withPatientId(patient.getId()).withResult("100").withTestDate(today).build();
            allLabResults.add(labResult);
            labResultIds.add(labResult.getId());
        }
        return labResultIds;
    }

    private Patient createPatient(MedicalHistory medicalHistory, Clinic clinic) {
        Patient patient = PatientBuilder.startRecording().withDefaults().withMedicalHistory(medicalHistory).withGender(genders.get(0)).build();
        patientService.create(patient, clinic.getId());
        return patient;
    }

    private TreatmentAdvice createTreatmentAdviceForPatient(Patient patient, LocalTime doseTime) {
        DrugDosage drugDosage = new DrugDosage();
        drugDosage.setDrugId(drugs.get(0).getId());
        drugDosage.setMorningTime(doseTime.toString());
        LocalDate startDate = today;
        drugDosage.setStartDate(startDate);
        drugDosage.setEndDate(startDate.plusYears(2));
        drugDosage.setDosageTypeId(dosageTypes.get(0).getId());
        drugDosage.setMealAdviceId(mealAdviceTypes.get(0).getId());
        Object[] brands = drugs.get(0).getBrands().toArray();
        drugDosage.setBrandId(((Brand) brands[0]).getCompanyId());
        Regimen regimen = regimens.get(0);
        Object[] drugCompositionGroups = regimen.getDrugCompositionGroups().toArray();
        TreatmentAdvice treatmentAdvice = TreatmentAdviceBuilder.startRecording().withDefaults().withDrugDosages(drugDosage).withPatientId(patient.getId()).withRegimenId(regimen.getId()).withDrugCompositionGroupId(((DrugCompositionGroup) drugCompositionGroups[0]).getId()).build();
        treatmentAdviceService.createRegimen(treatmentAdvice);
        return treatmentAdvice;
    }

    private ClinicVisit activatePatientForFirstTime(Patient patient) {
        allClinicVisits.addAppointmentCalendar(patient.getId());
        ClinicVisit clinicVisit = allClinicVisits.getBaselineVisit(patient.getId());
        patientService.activate(patient.getId());
        return clinicVisit;
    }

    private String phoneNumber(LocalDate today, int clinicIndex, int index) {
        String dateString = today.toString("YYYYMMdd");
        return clinicIndex + dateString + index;
    }

    private String patientId(LocalDate today, int clinicIndex, int index) {
        return phoneNumber(today, clinicIndex, index);
    }
}

