package org.motechproject.tamafunctionalframework.page;

import org.motechproject.tamafunctionalframework.framework.WebDriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Drug1Section extends DrugDosageSection {

    public Drug1Section(WebDriver webDriver) {
        super(webDriver);
        drugDosageTypeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[0].dosageTypeId_id"));
        drugStartDateElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[0].startDateAsDate_id"));
        drugMorningDosageTimeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[0].morningTime_id"));
        drugEveningDosageTimeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[0].eveningTime_id"));
        drugOffsetDaysElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[0].offsetDays_id"));
        drugMealAdviceTypeElement = webDriver.findElement(By.id("_treatmentAdvice.drugDosages[0].mealAdviceId_id"));
    }
}
