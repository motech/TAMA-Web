package org.motechproject.tama.domain;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

public abstract class CouchDocument extends BaseEntity {

	@TypeDiscriminator
	private String documentType;
	@JsonProperty("_id")
	private String id;
	@JsonProperty("_rev")
	private String revision;

	public CouchDocument() {
        this.documentType = this.getClass().getSimpleName();
	}

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
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CouchDocument that = (CouchDocument) o;

		if (id != null ? !id.equals(that.id) : that.id != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : 0;
	}


}
