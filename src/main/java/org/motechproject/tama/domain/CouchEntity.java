package org.motechproject.tama.domain;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.CouchDbDocument;
import org.ektorp.support.TypeDiscriminator;


public abstract class CouchEntity extends CouchDbDocument {

	private static final long serialVersionUID = 4517930750095396426L;

	@TypeDiscriminator
	private String documentType;

	public CouchEntity() {
        this.documentType = this.getClass().getSimpleName();
	}

	public String getDocumentType() {
	    return documentType;
	}

	public void setDocumentType(String documentType) {
	    this.documentType = documentType;
	}

	@JsonIgnore
	public Integer getVersion() {
	    if(getRevision() == null) return -1;
	    return Integer.parseInt( getRevision().split("-")[0]);
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CouchEntity that = (CouchEntity) o;

		if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return getId() != null ? getId().hashCode() : 0;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
	}
}
