package org.motechproject.tama.facility.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.tama.common.TAMAConstants;
import org.motechproject.tama.common.TAMAMessages;
import org.motechproject.tama.common.domain.CouchEntity;
import org.motechproject.tama.common.util.StringUtil;
import org.motechproject.tama.common.util.UUIDUtil;
import org.motechproject.tama.refdata.domain.City;
import org.springframework.validation.ObjectError;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

@TypeDiscriminator("doc.documentType == 'Clinic'")
public class Clinic extends CouchEntity implements Comparable<Clinic> {

    @NotNull
    private String name;

    @NotNull
    private String greetingName;

    @NotNull
    private String address;

    @NotNull
    private String phone;

    @Size(min = 1, max = 3, message = "Please enter the contact details for at least one clinician")
    @OneToMany
    @Valid

    private List<ClinicianContact> clinicianContacts = new LinkedList<ClinicianContact>();

    @ManyToOne
    private City city;

    private String cityId;

    protected Clinic() {
    }

    public Clinic(String id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public static Clinic newClinic() {
        return new Clinic();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGreetingName() {
        return greetingName;
    }

    public void setGreetingName(String greetingName) {
        this.greetingName = greetingName;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<ClinicianContact> getClinicianContacts() {
        return clinicianContacts;
    }

    public void setClinicianContacts(List<ClinicianContact> clinicianContacts) {
        this.clinicianContacts = clinicianContacts;
    }

    @JsonIgnore
    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
        this.cityId = city.getId();
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @Override
    public int compareTo(Clinic o) {
        return name.toLowerCase().compareTo(o.name.toLowerCase());
    }

    public ObjectError validateClinicianContact(List<ClinicianContact> clinicianContacts)
    {
        String validationMessage = null;
        ObjectError objectError = null;
        for(ClinicianContact clinicianContact: clinicianContacts)
        {
            if(StringUtils.isEmpty(clinicianContact.getName()) && StringUtils.isEmpty(clinicianContact.getPhoneNumber()))
            {
                  this.getClinicianContacts().remove(clinicianContact);

            }
            else if(StringUtils.isEmpty(clinicianContact.getName()))
            {
                 validationMessage ="Clinician name is to provided along with phone number";
            }
            else if(StringUtils.isEmpty(clinicianContact.getPhoneNumber()))
            {
                  validationMessage ="Clinician phone number is to provided along with Name ";
            }
        }
        if(validationMessage!=null)
        {
         objectError = new ObjectError(Clinic.class.getName(),validationMessage);
        }
        return objectError;
    }

    public static class ClinicianContact implements Serializable {

       // @NotNull(message = "Clinician name is mandatory")
        private String name;

        private String id;

       // @NotNull(message = "Phone number is mandatory")
        @Pattern(regexp = TAMAConstants.MOBILE_NUMBER_REGEX, message = TAMAMessages.MOBILE_NUMBER_REGEX_MESSAGE)
        private String phoneNumber;

        public ClinicianContact()
        {

        }
        public ClinicianContact(String name, String phoneNumber) {
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public String getId() {
            if(this.id == null)
            {
                this.id =  UUIDUtil.newUUID();
            }
            return this.id;
        }

        public void setId(String id) {
          this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}