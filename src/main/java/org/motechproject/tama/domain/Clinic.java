package org.motechproject.tama.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.TAMAConstants;
import org.motechproject.tama.TAMAMessages;
import org.motechproject.tama.repository.Cities;
import org.motechproject.tama.repository.Clinics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.NotNull;
import org.motechproject.tama.domain.City;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.resources.Messages;

import javax.persistence.ManyToOne;
import javax.validation.constraints.Pattern;
import java.io.IOException;

@RooJavaBean
@RooToString
@RooEntity
@TypeDiscriminator("doc.documentType == 'Clinic'")
public class Clinic extends CouchEntity{

    @Autowired
    private Clinics clinics;

    @Autowired
    private Cities cities;

    @NotNull
    private String name;

    @NotNull
    private String address;

    private String phone;

    @ManyToOne
    private City city ;

    private String cityId;

    protected Clinic() {
    }
    public Clinic(String id){
        this.setId(id);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Clinic newClinic() {
        return new Clinic();
    }

    public Clinics allClinics() {
        return clinics;
    }
}

