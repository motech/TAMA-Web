package org.motechproject.tama.builder;

import org.joda.time.DateTime;
import org.mockito.Matchers;
import org.motechproject.model.Time;
import org.motechproject.server.pillreminder.contract.DosageResponse;
import org.motechproject.tama.domain.DosageTimeLine;
import org.motechproject.tama.domain.TAMAPillRegimen;
import org.motechproject.tama.ivr.DosageResponseWithDate;
import org.motechproject.util.DateUtil;

import java.util.Collections;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TAMAPillRegimenBuilder {

    private TAMAPillRegimen tamaPillRegimen;

    private TAMAPillRegimenBuilder(){
        tamaPillRegimen = mock(TAMAPillRegimen.class);
    }

    public static TAMAPillRegimenBuilder startRecording(){
        return new TAMAPillRegimenBuilder();
    }

    public TAMAPillRegimenBuilder withTwoDosagesFrom(DateTime dateTime) {
        DosageTimeLine dosageTimeLine = mock(DosageTimeLine.class);
        DosageResponseWithDate dosageResponse1 = new DosageResponseWithDate(new DosageResponse("dosageId1", new Time(10, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        DosageResponseWithDate dosageResponse2 = new DosageResponseWithDate(new DosageResponse("dosageId2", new Time(11, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        when(tamaPillRegimen.getDosageTimeLine(same(dateTime), Matchers.<DateTime>any())).thenReturn(dosageTimeLine);
        when(dosageTimeLine.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(dosageTimeLine.next()).thenReturn(dosageResponse1).thenReturn(dosageResponse2);
        return this;
    }

    public TAMAPillRegimenBuilder withThreeDosagesInTotal() {
        DosageTimeLine dosageTimeLine = mock(DosageTimeLine.class);
        DosageResponseWithDate dosageResponse1 = new DosageResponseWithDate(new DosageResponse("dosageId1", new Time(10, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        DosageResponseWithDate dosageResponse2 = new DosageResponseWithDate(new DosageResponse("dosageId2", new Time(11, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        DosageResponseWithDate dosageResponse3 = new DosageResponseWithDate(new DosageResponse("dosageId3", new Time(12, 10), DateUtil.today(), null, null, Collections.EMPTY_LIST), DateUtil.today());
        when(tamaPillRegimen.getDosageTimeLine()).thenReturn(dosageTimeLine);
        when(dosageTimeLine.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);
        when(dosageTimeLine.next()).thenReturn(dosageResponse1).thenReturn(dosageResponse2).thenReturn(dosageResponse3);
        return this;
    }

    public TAMAPillRegimen build(){
        return tamaPillRegimen;
    }
}