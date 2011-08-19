package org.motechproject.tamafunctional.testdata.treatmentadvice;

import org.apache.commons.lang.NotImplementedException;
import org.motechproject.tamafunctional.testdata.TestEntity;

public class TestDrugDosage  extends TestEntity {
    private String dosageSchedule;
    private String mealAdvice;
    private String brandName;
    private String dosageType;

    private TestDrugDosage() {
    }

    public static TestDrugDosage withExtrinsic() {
        return new TestDrugDosage().mealAdvice("After Meal").brandName(unique("Foo"));
    }

    public static TestDrugDosage forMorning() {
        return withExtrinsic().dosageType("Morning Daily").dosageSchedule("10.00");
    }

    public static TestDrugDosage forEvening() {
        return withExtrinsic().dosageType("Evening Daily").dosageSchedule("10.00");
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

    @Override
    public String resourceName() {
        throw new NotImplementedException();
    }
}
