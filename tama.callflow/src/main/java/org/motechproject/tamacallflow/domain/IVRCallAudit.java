package org.motechproject.tamacallflow.domain;

import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.util.DateUtil;

@TypeDiscriminator("doc.documentType == 'IVRCallAudit'")
public class IVRCallAudit extends CouchEntity {
    private String cid;
    private String sid;
    private String patientId;
    private State state;
    private DateTime dateTime;

    public IVRCallAudit() {
    }

    public IVRCallAudit(String cid, String sid, String patientId, State state) {
        this.cid = cid;
        this.sid = sid;
        this.patientId = patientId;
        this.state = state;
        this.dateTime = DateUtil.now();
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public enum State {
        PASSCODE_ENTRY_FAILED {
            @Override
            public String toString() {
                return "passcode entry failed";
            }
        }, USER_NOT_FOUND {
            @Override
            public String toString() {
                return "user not found";
            }
        }, USER_AUTHORISED {
            @Override
            public String toString() {
                return "user authorised";
            }
        };
    }
}
