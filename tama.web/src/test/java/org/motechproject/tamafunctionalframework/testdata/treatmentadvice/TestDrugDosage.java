package org.motechproject.tamafunctionalframework.testdata.treatmentadvice;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.tamafunctionalframework.testdata.TestEntity;
import org.motechproject.util.DateUtil;

import java.util.ArrayList;

public class TestDrugDosage extends TestEntity {
    public static String MORNING_DAILY = "Morning Daily";
    public static String EVENING_DAILY = "Evening Daily";
    private String dosageSchedule;
    private String mealAdvice;
    private String brandName;
    private String dosageType;
    private LocalDate startDate;

    private TestDrugDosage() {
    }

    public static TestDrugDosage withExtrinsic() {
        return new TestDrugDosage().mealAdvice("After Meal").brandName(unique("Foo"));
    }

    public static TestDrugDosage[] create(String... brandNames) {
        final DateTime now = DateUtil.now();
        return create(now.toLocalDate(), now.toLocalTime(), brandNames);
    }

    public static TestDrugDosage[] create(LocalDate startDate, LocalTime doseTime, String... brandNames) {
        String dosageType = doseTime.getHourOfDay() <= 11 ? MORNING_DAILY : EVENING_DAILY;
        LocalTime hourNowIn12HourFormat = doseTime.getHourOfDay() > 12 ? doseTime.minusHours(12) : doseTime;
        String dosageSchedule = hourNowIn12HourFormat.toString("HH:mm");
        ArrayList<TestDrugDosage> drugDosages = new ArrayList<TestDrugDosage>();
        for (String brandName : brandNames) {
            drugDosages.add(withExtrinsic().dosageType(dosageType).dosageSchedule(dosageSchedule).brandName(brandName).startDate(startDate));
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

    public String startDate() {
        return startDate.toString("dd/MM/yyyy");
    }

    public TestDrugDosage startDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
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
