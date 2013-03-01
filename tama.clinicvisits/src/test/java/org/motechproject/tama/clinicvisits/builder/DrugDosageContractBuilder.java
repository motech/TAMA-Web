package org.motechproject.tama.clinicvisits.builder;

import org.motechproject.tama.patient.contract.DrugDosageContract;
import org.motechproject.util.DateUtil;

import java.util.Date;

public class DrugDosageContractBuilder {

    DrugDosageContract dosage = new DrugDosageContract();

    public DrugDosageContractBuilder(){
            this.withDrugName("Drug")
                    .withDosageType("Type1")
                    .withMorningTime("8:00 AM")
                    .withStartDate(DateUtil.today().toDate());

    }

    public DrugDosageContractBuilder withDrugName(String drug){
        dosage.setDrugName(drug);
        return this;
    }

    public DrugDosageContractBuilder withDrugId(String drugId){
        dosage.setDrugId(drugId);
        return this;
    }

    public DrugDosageContractBuilder withBrandId(String brandId){
        dosage.setBrandId(brandId);
        return this;
    }

    public DrugDosageContractBuilder withDosageType(String dosageType){
        dosage.setDosageType(dosageType);
        return this;
    }

    public DrugDosageContractBuilder withOffsetDays(int offsetDays){
        dosage.setOffsetDays(offsetDays);
        return this;
    }

    public DrugDosageContractBuilder withMorningTime(String morningTime){
        dosage.setMorningTime(morningTime);
        return this;
    }

    public DrugDosageContractBuilder withEveningTime(String eveningTime){
        dosage.setEveningTime(eveningTime);
        return this;
    }

    public DrugDosageContractBuilder withStartDate(Date startDate){
        dosage.setStartDate(startDate);
        return this;
    }

    public DrugDosageContractBuilder withEndDate(Date endDate){
        dosage.setEndDate(endDate);
        return this;
    }

    public DrugDosageContractBuilder withAdvice(String advice){
        dosage.setAdvice(advice);
        return this;
    }

    public DrugDosageContractBuilder withMealAdvice(String mealAdvice){
        dosage.setMealAdvice(mealAdvice);
        return this;
    }

    public DrugDosageContract build(){
        return dosage;
    }


}
