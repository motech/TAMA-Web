// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.motechproject.tama.web;

import java.lang.String;
import org.motechproject.tama.domain.Brand;
import org.motechproject.tama.domain.Company;
import org.motechproject.tama.domain.Doctor;
import org.motechproject.tama.domain.Drug;
import org.motechproject.tama.domain.Gender;
import org.motechproject.tama.domain.IVRLanguage;
import org.motechproject.tama.domain.Patient;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

privileged aspect ApplicationConversionServiceFactoryBean_Roo_ConversionService {
    
    public void ApplicationConversionServiceFactoryBean.installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(new BrandConverter());
        registry.addConverter(new CompanyConverter());
        registry.addConverter(new DoctorConverter());
        registry.addConverter(new DrugConverter());
        registry.addConverter(new GenderConverter());
        registry.addConverter(new IVRLanguageConverter());
        registry.addConverter(new PatientConverter());
    }
    
    public void ApplicationConversionServiceFactoryBean.afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.BrandConverter implements Converter<Brand, String>  {
        public String convert(Brand brand) {
            return new StringBuilder().append(brand.getName()).toString();
        }
        
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.CompanyConverter implements Converter<Company, String>  {
        public String convert(Company company) {
            return new StringBuilder().append(company.getName()).toString();
        }
        
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.DoctorConverter implements Converter<Doctor, String>  {
        public String convert(Doctor doctor) {
            return new StringBuilder().append(doctor.getDoctorId()).append(" ").append(doctor.getFirstName()).append(" ").append(doctor.getLastName()).toString();
        }
        
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.DrugConverter implements Converter<Drug, String>  {
        public String convert(Drug drug) {
            return new StringBuilder().append(drug.getName()).toString();
        }
        
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.GenderConverter implements Converter<Gender, String>  {
        public String convert(Gender gender) {
            return new StringBuilder().append(gender.getType()).toString();
        }
        
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.IVRLanguageConverter implements Converter<IVRLanguage, String>  {
        public String convert(IVRLanguage iVRLanguage) {
            return new StringBuilder().append(iVRLanguage.getName()).toString();
        }
        
    }
    
    static class org.motechproject.tama.web.ApplicationConversionServiceFactoryBean.PatientConverter implements Converter<Patient, String>  {
        public String convert(Patient patient) {
            return new StringBuilder().append(patient.getPatientId()).append(" ").append(patient.getPasscode()).append(" ").append(patient.getMobilePhoneNumber()).append(" ").append(patient.getDateOfBirth()).toString();
        }
        
    }
    
}
