package org.motechproject.tamaperformance;

import org.motechproject.tamafunctionalframework.testdata.TestClinic;
import org.motechproject.tamafunctionalframework.testdata.TestClinician;
import org.motechproject.tamafunctionalframework.testdata.TestPatient;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestDrugDosage;
import org.motechproject.tamafunctionalframework.testdata.treatmentadvice.TestTreatmentAdvice;

import java.util.ArrayList;
import java.util.List;

public class TestSample {
    private TestClinic clinic1 = TestClinic.withMandatory().name("clinic1");
    private TestClinic clinic2 = TestClinic.withMandatory().name("clinic2");
    private TestClinic clinic3 = TestClinic.withMandatory().name("clinic3");

    public List<TestClinic> clinics = new ArrayList<TestClinic>() {{
        add(clinic1);
        add(clinic2);
        add(clinic3);
    }};

    private TestClinician clinician1 = TestClinician.withMandatory().name("clinician1").userName("clinician1").clinic(clinic1);
    private TestClinician clinician2 = TestClinician.withMandatory().name("clinician2").userName("clinician2").clinic(clinic2);
    private TestClinician clinician3 = TestClinician.withMandatory().name("clinician3").userName("clinician3").clinic(clinic3);

    public List<TestClinician> clinicians = new ArrayList<TestClinician>() {{
        add(clinician1);
        add(clinician2);
        add(clinician3);
    }};

    public TestTreatmentAdvice treatmentAdvice = TestTreatmentAdvice.withExtrinsic(TestDrugDosage.create("Efferven", "Combivir"));

    private TestPatient patient1 = TestPatient.withMandatory().patientId("p1").mobileNumber("1111111111");
    private TestPatient patient2 = TestPatient.withMandatory().patientId("p2").mobileNumber("2222222222");
    private TestPatient patient3 = TestPatient.withMandatory().patientId("p3").mobileNumber("3333333333");
    private TestPatient patient4 = TestPatient.withMandatory().patientId("p4").mobileNumber("4444444444");
    private TestPatient patient5 = TestPatient.withMandatory().patientId("p5").mobileNumber("5555555555");
    private TestPatient patient6 = TestPatient.withMandatory().patientId("p6").mobileNumber("6666666666");
    public List<TestPatient> patients = new ArrayList<TestPatient>() {{
        add(patient1);
        add(patient2);
        add(patient3);
        add(patient4);
        add(patient5);
        add(patient6);
    }};
}
