package org.motechproject.tamaperformance;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.tama.ivr.TamaIVRMessage;
import org.motechproject.tamafunctional.framework.MyWebClient;
import org.motechproject.tamafunctional.ivr.Caller;
import org.motechproject.tamafunctional.test.ivr.BaseIVRTest;
import org.motechproject.tamafunctional.testdata.OutboxCallInfo;
import org.motechproject.tamafunctional.testdata.TestClinic;
import org.motechproject.tamafunctional.testdata.TestClinician;
import org.motechproject.tamafunctional.testdata.TestPatient;
import org.motechproject.tamafunctional.testdata.ivrreponse.IVRResponse;
import org.motechproject.tamafunctional.testdata.treatmentadvice.TestTreatmentAdvice;
import org.motechproject.tamafunctional.testdataservice.ClinicDataService;
import org.motechproject.tamafunctional.testdataservice.ClinicianDataService;
import org.motechproject.tamafunctional.testdataservice.PatientDataService;
import org.motechproject.util.DateUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;
import static org.motechproject.tamafunctional.test.ivr.IVRAssert.asksForCollectDtmfWith;
import static org.motechproject.tamafunctional.test.ivr.IVRAssert.assertAudioFilesPresent;

/**
 * How to Run : <pre> mvn -Dtest=org.motechproject.tamaperformance.IVRCallTest test </pre>
 */
public class IVRCallTest extends BaseIVRTest {
    private int callCount = 200;

    @Before
    public void setup() {
        TestSample testSample = new TestSample();

        for (TestClinic clinic : testSample.clinics) {
            new ClinicDataService(webDriver).create(clinic);
        }

        for (int i = 0; i < testSample.clinicians.size(); i++) {
            TestClinician clinician = testSample.clinicians.get(i);

            new ClinicianDataService(webDriver).create(clinician);

            createPatientWithARTRegimen(testSample.patients.get(i * 2), testSample.treatmentAdvice, clinician);
            createPatientWithARTRegimen(testSample.patients.get(i * 2 + 1), testSample.treatmentAdvice, clinician);
        }
    }

    private void createPatientWithARTRegimen(TestPatient patient, TestTreatmentAdvice treatmentAdvice, TestClinician clinician) {
        new PatientDataService(webDriver).registerAndActivate(patient, clinician);
        new PatientDataService(webDriver).createARTRegimen(treatmentAdvice, patient, clinician);
    }

    @Test
    public void testWithPatient() throws Throwable {
        List<Callable<Object>> callables = new ArrayList<Callable<Object>>();
        callables.add(executePillReminderCallFlow("p1", "clinic1"));
        callables.add(executePillReminderCallFlow("p2", "clinic1"));
        callables.add(executePillReminderCallFlow("p3", "clinic2"));
        callables.add(executePillReminderCallFlow("p4", "clinic2"));
        callables.add(executePillReminderCallFlow("p5", "clinic3"));
        callables.add(executePillReminderCallFlow("p6", "clinic3"));
        callables.add(executeOutboxCallInfo("p1"));
        callables.add(executeOutboxCallInfo("p2"));
        callables.add(executeOutboxCallInfo("p3"));
        callables.add(executeOutboxCallInfo("p4"));
        callables.add(executeOutboxCallInfo("p5"));
        callables.add(executeOutboxCallInfo("p6"));

        List<Future<Object>> futures = Executors.newFixedThreadPool(10).invokeAll(callables);
        try {
            for (Future<Object> future : futures) {
                future.get();
            }
        } catch (ExecutionException e) {
            throw e.getCause();
        }

    }

    private Callable<Object> executeOutboxCallInfo(final String patientId) {
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {

                MyWebClient webClient = new MyWebClient();
                final TestSample testSample = new TestSample();
                TestPatient patient = selectFirst(testSample.patients, having(on(TestPatient.class).patientId(), equalTo(patientId)));

                OutboxCallInfo callInfo = new OutboxCallInfo();
                for (int i = 1; i < 20; i++) {
                    System.out.println("OutBox call " + i);
                    Caller caller = new Caller("sid" + DateUtil.now().toInstant().getMillis(), patient.mobileNumber(), webClient);
                    IVRResponse ivrResponse = caller.replyToCall(callInfo);
                    assertAudioFilesPresent(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);
                    ivrResponse = caller.enter("1234");
                    assertAudioFilesPresent(ivrResponse, TamaIVRMessage.FILE_050_03_01_ITS_TIME_FOR_BEST_CALL_TIME);
                    caller.hangup();
                }
                return null;
            }
        };
    }

    private Callable<Object> executePillReminderCallFlow(final String patientId, final String clinicName)
            throws IOException {
        final TestSample testSample = new TestSample();
        return new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                MyWebClient webClient = new MyWebClient();
                TestPatient patient = selectFirst(testSample.patients, having(on(TestPatient.class).patientId(), equalTo(patientId)));
                TestClinic clinic = selectFirst(testSample.clinics, having(on(TestClinic.class).name(), equalTo(clinicName)));
                for (int i = 1; i <= callCount; i++) {
                    System.out.println("************************* " + i + " *************************");
                    callTama(patient, clinic, webClient);
                }
                return null;
            }
        };
    }

    private void callTama(TestPatient patient, TestClinic clinic, MyWebClient webClient) throws IOException {

        Caller caller = new Caller("sid" + DateUtil.now().toInstant().getMillis(), patient.mobileNumber(), webClient);
        IVRResponse ivrResponse = caller.call();
        asksForCollectDtmfWith(ivrResponse, TamaIVRMessage.SIGNATURE_MUSIC);

        ivrResponse = caller.enter("1234#");
        asksForCollectDtmfWith(ivrResponse, welcomeAudioForClinic(clinic), TamaIVRMessage.ITS_TIME_FOR_THE_PILL_INCOMING_CALL_INSIDE_PILL_WINDOW, "pillazt3tc_combivir", "pillefv_efavir", TamaIVRMessage.FROM_THE_BOTTLE_INCOMING_CALL_INSIDE_PILL_WINDOW);

        ivrResponse = caller.enter("3#");
        assertAudioFilesPresent(ivrResponse, TamaIVRMessage.NO_MESSAGES);

        caller.hangup();
    }

    private String welcomeAudioForClinic(TestClinic clinic) {
        return "welcome_to_" + clinic.name();
    }


}

