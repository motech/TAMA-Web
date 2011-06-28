package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

public class BaseEntity {

    @TypeDiscriminator
    private String documentType;

    public BaseEntity() {
        this.documentType = this.getClass().getSimpleName();
    }

    @JsonProperty("_id")
    private String id;

    @JsonProperty("_rev")
    private String revision;


    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnore
    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    @JsonIgnore
    public Integer getVersion() {
        if(revision == null) return -1;
        return Integer.parseInt( revision.split("-")[0]);
    }
}
