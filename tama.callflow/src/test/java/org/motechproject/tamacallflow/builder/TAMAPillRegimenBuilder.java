package org.motechproject.tamacallflow.builder;

import org.joda.time.DateTime;
import org.mockito.Matchers;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tamacallflow.domain.DosageTimeLine;
import org.motechproject.tamacallflow.domain.PillRegimen;
import org.motechproject.tamacallflow.ivr.Dose;
import org.motechproject.util.DateUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TAMAPillRegimenBuilder {

    private PillRegimen pillRegimen;
    private Properties properties;

    private TAMAPillRegimenBuilder(){
        pillRegimen = mock(PillRegimen.class);
    }

    public static TAMAPillRegimenBuilder startRecording(){
        return new TAMAPillRegimenBuilder();
    }

    public TAMAPillRegimenBuilder withTwoDosagesFrom(DateTime dateTime) {
        DosageTimeLine dosageTimeLine = mock(DosageTimeLine.class);
        Dose doseResponse1 = new Dose(new DosageResponse("dosageId1", new Time(10, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        Dose doseResponse2 = new Dose(new DosageResponse("dosageId2", new Time(11, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        when(pillRegimen.getDosageResponses()).thenReturn(Arrays.<DosageResponse>asList(doseResponse1, doseResponse2));
        when(pillRegimen.getDosageTimeLine(Matchers.<DateTime>any(), Matchers.<DateTime>any())).thenReturn(dosageTimeLine);
        when(dosageTimeLine.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(dosageTimeLine.next()).thenReturn(doseResponse1).thenReturn(doseResponse2);
        return this;
    }

    public TAMAPillRegimenBuilder withThreeDosagesInTotal() {
        DosageTimeLine dosageTimeLine = mock(DosageTimeLine.class);
        Dose doseResponse1 = new Dose(new DosageResponse("dosageId1", new Time(10, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        Dose doseResponse2 = new Dose(new DosageResponse("dosageId2", new Time(11, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        Dose doseResponse3 = new Dose(new DosageResponse("dosageId3", new Time(12, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        when(pillRegimen.getDosageResponses()).thenReturn(Arrays.<DosageResponse>asList(doseResponse1, doseResponse2, doseResponse3));
        when(pillRegimen.getDosageTimeLine()).thenReturn(dosageTimeLine);
        when(dosageTimeLine.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(dosageTimeLine.next()).thenReturn(doseResponse1).thenReturn(doseResponse2).thenReturn(doseResponse3);
        return this;
    }

    public PillRegimen build(){
        return pillRegimen;
    }
}