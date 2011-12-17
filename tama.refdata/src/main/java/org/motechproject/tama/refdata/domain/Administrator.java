package org.motechproject.tama.refdata.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tamacommon.domain.CouchEntity;
import org.motechproject.tamacommon.domain.TAMAUser;

@TypeDiscriminator("doc.documentType == 'Administrator'")
public class Administrator extends CouchEntity implements TAMAUser {
    private String name;
    private String username;
    private String password;
    private String encryptedPassword;

    public Administrator() {

    }

    public Administrator(String name, String username, String password) {
        this.username = username;
        this.name = name;
        this.password = password;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    @JsonIgnore
    public String getClinicId() {
        return StringUtils.EMPTY;
    }

    @Override
    @JsonIgnore
    public String getClinicName() {
        return StringUtils.EMPTY;
    }

    @JsonIgnore
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean credentialsAre(String password) {
        return this.password.equals(password);
    }
}
