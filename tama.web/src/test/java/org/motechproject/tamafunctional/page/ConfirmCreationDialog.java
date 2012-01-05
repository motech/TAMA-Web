package org.motechproject.tamafunctional.page;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ConfirmCreationDialog {

    @FindBy(how = How.ID, using = "four_week_warning_confirm")
    private WebElement confirm;
    @FindBy(how = How.ID, using = "four_week_warning_cancel")
    private WebElement cancel;

    public void confirm(WebDriverWait wait) {
        wait.until(new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                return confirm.isEnabled();
            }
        });
        confirm.click();
    }
}
