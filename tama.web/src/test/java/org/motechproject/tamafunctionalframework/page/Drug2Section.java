package org.motechproject.tamafunctionalframework.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Drug2Section extends DrugDosageSection {

    public Drug2Section(WebDriver webDriver) {
        super(webDriver);
        drugDosageTypeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].dosageTypeId_id"));
        drugStartDateElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].startDateAsDate_id"));
        drugBrandElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].brandId_id"));
        drugMorningDosageTimeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].morningTime_id"));
        drugEveningDosageTimeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].eveningTime_id"));
        drugOffsetDaysElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].offsetDays_id"));
        drugMealAdviceTypeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[1].mealAdviceId_id"));
    }
}
