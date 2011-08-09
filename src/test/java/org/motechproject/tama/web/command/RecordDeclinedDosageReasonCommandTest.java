package org.motechproject.tama.web.command;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.tama.domain.DosageAdherenceLog;
import org.motechproject.tama.domain.DosageStatus;
import org.motechproject.tama.ivr.IVRCallAttribute;
import org.motechproject.tama.ivr.IVRContext;
import org.motechproject.tama.ivr.IVRRequest;
import org.motechproject.tama.ivr.IVRSession;
import org.motechproject.tama.ivr.call.PillReminderCall;
import org.motechproject.tama.repository.DosageAdherenceLogs;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class RecordDeclinedDosageReasonCommandTest {
    private String pillRegimenId;
     private String dosageId;
     private String patientId;
     private String userInput;
     private Map<String,String> params;
     private IVRContext context;
     private DosageAdherenceLog log;

     @Mock
     private DosageAdherenceLogs logs;
     @Mock
     private IVRRequest req;
     @Mock
     private IVRSession session;

     @Before
     public void setup() {
         initMocks(this);

         pillRegimenId = "pillRegimenId";
         dosageId = "dosageId";
         patientId = "test";
         userInput = "1";
         params = new HashMap<String, String>();
         params.put(PillReminderCall.DOSAGE_ID, dosageId);
         context = new IVRContext(req, session);

         when(req.getTamaParams()).thenReturn(params);
         when(session.get(IVRCallAttribute.PATIENT_DOC_ID)).thenReturn(patientId);
         when(req.getInput()).thenReturn(userInput);
     }

     @Test
     public void shouldUpdateTheDeclinedDosageAdherenceLogWithReason() {
         log = new DosageAdherenceLog(patientId, pillRegimenId, dosageId, DosageStatus.NOT_TAKEN);
         when(logs.findByDosageIdAndDate(eq(dosageId), any(LocalDate.class))).thenReturn(log);

         RecordDeclinedDosageReasonCommand command = new RecordDeclinedDosageReasonCommand(logs);
         command.execute(context);

         verify(logs).update(any(DosageAdherenceLog.class));
     }
}
