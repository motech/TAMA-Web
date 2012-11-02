package org.motechproject.tama.common.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

public class BasicTAMAUser extends CouchEntity implements TAMAUser {

    private String name;
    private String username;
    private String password;
    private String encryptedPassword;

    public BasicTAMAUser() {
    }

    public BasicTAMAUser(String name, String username, String password) {
        this.name = name;
        this.username = username;
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
