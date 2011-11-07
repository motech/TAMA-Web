package org.motechproject.tama.web;

import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.roo.addon.web.mvc.controller.RooConversionService;
import org.springframework.core.convert.converter.Converter;
import org.motechproject.tama.domain.*;

/**
 * A central place to register application Converters and Formatters. 
 */
@RooConversionService
public class ApplicationConversionServiceFactoryBean extends FormattingConversionServiceFactoryBean {

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}
	
    public void installLabelConverters(FormatterRegistry registry) {
        registry.addConverter(new BrandConverter());
        registry.addConverter(new ClinicConverter());
        registry.addConverter(new ClinicianConverter());
        registry.addConverter(new CompanyConverter());
        registry.addConverter(new DosageTypeConverter());
        registry.addConverter(new DrugConverter());
        registry.addConverter(new GenderConverter());
        registry.addConverter(new IVRLanguageConverter());
        registry.addConverter(new MealAdviceTypeConverter());
        registry.addConverter(new PatientConverter());
        registry.addConverter(new RegimenConverter());
        registry.addConverter(new TreatmentAdviceConverter());
        registry.addConverter(new CityConverter());
    }
    
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        installLabelConverters(getObject());
    }
    
    static class BrandConverter implements Converter<Brand, String>  {
        public String convert(Brand brand) {
            return new StringBuilder().append(brand.getName()).append(" ").append(brand.getCompanyId()).toString();
        }
        
    }
    
    static class ClinicConverter implements Converter<Clinic, String>  {
        public String convert(Clinic clinic) {
            return new StringBuilder().append(clinic.getName()).append(", ").append(clinic.getCity()).toString();
        }
        
    }
    
    static class ClinicianConverter implements Converter<Clinician, String>  {
        public String convert(Clinician clinician) {
            return new StringBuilder().append(clinician.getName()).append(" ").append(clinician.getUsername()).append(" ").append(clinician.getContactNumber()).append(" ").append(clinician.getAlternateContactNumber()).toString();
        }
        
    }
    
    static class CompanyConverter implements Converter<Company, String>  {
        public String convert(Company company) {
            return new StringBuilder().append(company.getName()).toString();
        }
        
    }

    static class DosageTypeConverter implements Converter<DosageType, String>  {
        public String convert(DosageType dosageType) {
            return new StringBuilder().append(dosageType.getType()).toString();
        }
        
    }
    
    static class DrugConverter implements Converter<Drug, String>  {
        public String convert(Drug drug) {
            return new StringBuilder().append(drug.getName()).toString();
        }
        
    }
    
    static class GenderConverter implements Converter<Gender, String>  {
        public String convert(Gender gender) {
            return new StringBuilder().append(gender.getType()).toString();
        }
        
    }
    
    static class IVRLanguageConverter implements Converter<IVRLanguage, String>  {
        public String convert(IVRLanguage iVRLanguage) {
            return new StringBuilder().append(iVRLanguage.getName()).toString();
        }
        
    }
    
    static class MealAdviceTypeConverter implements Converter<MealAdviceType, String>  {
        public String convert(MealAdviceType mealAdviceType) {
            return new StringBuilder().append(mealAdviceType.getType()).toString();
        }
        
    }
    
    static class PatientConverter implements Converter<Patient, String>  {
        public String convert(Patient patient) {
            return new StringBuilder().append(patient.getPatientId()).append(" ").append(patient.getPatientPreferences().getPasscode()).append(" ").append(patient.getMobilePhoneNumber()).append(" ").append(patient.getDateOfBirthAsDate()).toString();
        }
        
    }
    
    static class RegimenConverter implements Converter<Regimen, String>  {
        public String convert(Regimen regimen) {
            return new StringBuilder().append(regimen.getName()).append(" ").append(regimen.getDisplayName()).toString();
        }
        
    }
    
    static class TreatmentAdviceConverter implements Converter<TreatmentAdvice, String>  {
        public String convert(TreatmentAdvice treatmentAdvice) {
            return new StringBuilder().append(treatmentAdvice.getPatientId()).append(" ").append(treatmentAdvice.getRegimenId()).append(" ").append(treatmentAdvice.getDrugCompositionId()).toString();
        }
        
    }

    static class CityConverter implements Converter<City, String>  {
        public String convert(City city) {
            return new StringBuilder().append(city.getName()).toString();
        }

    }
}
