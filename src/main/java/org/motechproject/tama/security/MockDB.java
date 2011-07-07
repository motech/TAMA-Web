package org.motechproject.tama.security;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;

public class MockDB {
    public GrantedAuthority authorize(String username, String password) {
        if(username.equals("1") && password.equals("1")) throw new EmptyResultDataAccessException(1);
        if(username.equals("2") && password.equals("2")) throw new EntityNotFoundException("bad");
        if(username.equals("3") && password.equals("3")) throw new NonUniqueResultException("bad");
        if(username.equals("4") && password.equals("4")) return Role.ADMIN.authority();
        if(username.equals("5") && password.equals("5")) return Role.CLINICIAN_DOCTOR.authority();
        if(username.equals("6") && password.equals("6")) return Role.CLINICIAN_STUDY_NURSE.authority();
        return null;
    }
}
