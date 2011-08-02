package org.motechproject.tama.domain;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.motechproject.tama.util.DateUtility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@TypeDiscriminator("doc.documentType == 'DosageAdherenceLog'")
public class DosageAdherenceLog extends CouchEntity{

    private String patientId;

    private String regimenId;

    private String dosageId;

    private LocalDate dosageDate;

    private DosageStatus dosageStatus;

    public DosageAdherenceLog() {
    }

    public DosageAdherenceLog(String patientId, String regimenId, String dosageId, DosageStatus dosageStatus) {
        this.patientId = patientId;
        this.regimenId = regimenId;
        this.dosageId = dosageId;
        this.dosageDate = new LocalDate(DateTimeZone.UTC);
        this.dosageStatus = dosageStatus;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getRegimenId() {
        return regimenId;
    }

    public void setRegimenId(String regimenId) {
        this.regimenId = regimenId;
    }

    public String getDosageId() {
        return dosageId;
    }

    public void setDosageId(String dosageId) {
        this.dosageId = dosageId;
    }

    public LocalDate getDosageDate() {
        return dosageDate;
    }

    public void setDosageDate(LocalDate dosageDate) {
        this.dosageDate = dosageDate;
    }

    public DosageStatus getDosageStatus() {
        return dosageStatus;
    }

    public void setDosageStatus(DosageStatus dosageStatus) {
        this.dosageStatus = dosageStatus;
    }

}
