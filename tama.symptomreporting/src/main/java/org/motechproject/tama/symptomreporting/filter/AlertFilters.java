package org.motechproject.tama.symptomreporting.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class AlertFilters {

    protected FirstPriorityFilter firstPriorityFilter;
    protected SecondPriorityFilter secondPriorityFilter;
    protected ThirdPriorityFilter thirdPriorityFilter;
    protected FourthPriorityFilter fourthPriorityFilter;
    protected FifthPriorityFilter fifthPriorityFilter;

    public AlertFilters() {

    }

    @Autowired
    public AlertFilters(FirstPriorityFilter firstPriorityFilter,
                        SecondPriorityFilter secondPriorityFilter,
                        ThirdPriorityFilter thirdPriorityFilter,
                        FourthPriorityFilter fourthPriorityFilter,
                        FifthPriorityFilter fifthPriorityFilter) {

        this.firstPriorityFilter = firstPriorityFilter;
        this.secondPriorityFilter = secondPriorityFilter;
        this.thirdPriorityFilter = thirdPriorityFilter;
        this.fourthPriorityFilter = fourthPriorityFilter;
        this.fifthPriorityFilter = fifthPriorityFilter;
    }

    public List<TreeNodeFilter> getAll() {
        return Arrays.asList(firstPriorityFilter, secondPriorityFilter, thirdPriorityFilter,
                fourthPriorityFilter, fifthPriorityFilter);
    }
}
