package org.motechproject.tamafunctional.testdata.treatmentadvice;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.motechproject.tamafunctional.testdata.TestEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

public class TestDrugDosage  extends TestEntity {
    public static String MORNING_DAILY = "Morning Daily";
    public static String EVENING_DAILY = "Evening Daily";
    private String dosageSchedule;
    private String mealAdvice;
    private String brandName;
    private String dosageType;

    private TestDrugDosage() {
    }

    public static TestDrugDosage withExtrinsic() {
        return new TestDrugDosage().mealAdvice("After Meal").brandName(unique("Foo"));
    }

    public static TestDrugDosage[] create(String... brandNames) {
        DateTime now = DateUtil.now();
        String dosageType = now.getHourOfDay() <= 11 ? MORNING_DAILY : EVENING_DAILY;
        DateTime hourNowIn12HourFormat = now.getHourOfDay() > 12 ? now.minusHours(12) : now;
        String dosageSchedule = hourNowIn12HourFormat.toString("HH:mm");
        ArrayList<TestDrugDosage> drugDosages = new ArrayList<TestDrugDosage>();
        for (String brandName : brandNames) {
            drugDosages.add(withExtrinsic().dosageType(dosageType).dosageSchedule(dosageSchedule).brandName(brandName));
        }
        return drugDosages.toArray(new TestDrugDosage[brandNames.length]);
    }

    public TestDrugDosage dosageType(String dosageType) {
        this.dosageType = dosageType;
        return this;
    }

    public String dosageType() {
        return dosageType;
    }

    public TestDrugDosage brandName(String brandName) {
        this.brandName = brandName;
        return this;
    }

    public String brandName() {
        return brandName;
    }

    public TestDrugDosage mealAdvice(String mealAdvice) {
        this.mealAdvice = mealAdvice;
        return this;
    }

    public String mealAdvice() {
        return mealAdvice;
    }

    public TestDrugDosage dosageSchedule(String dosageSchedule) {
        this.dosageSchedule = dosageSchedule;
        return this;
    }

    public String dosageSchedule() {
        return dosageSchedule;
    }

    public boolean isMorningDosage() {
        return dosageType.equals(MORNING_DAILY);
    }

    @Override
    public String resourceName() {
        throw new NotImplementedException();
    }
}
