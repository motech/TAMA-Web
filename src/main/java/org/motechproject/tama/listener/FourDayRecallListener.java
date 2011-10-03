package org.motechproject.tama.listener;

/*@Component
public class FourDayRecallListener {
    public static final String PATIENT_ID_KEY = "patient_id";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";

    private TamaSchedulerService schedulerService;

    public FourDayRecallListener() {
    }

    @Autowired
    public FourDayRecallListener(TamaSchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @MotechListener(subjects = TAMAConstants.FOUR_DAY_RECALL_SUBJECT)
    public void handle(MotechEvent motechEvent) {
        String patientId = motechEvent.getParameters().get(PATIENT_ID_KEY).toString();
        LocalDate startDate = (LocalDate) motechEvent.getParameters().get(START_DATE);
        LocalDate endDate = (LocalDate) motechEvent.getParameters().get(END_DATE);
        schedulerService.scheduleRepeatingJobsForFourDayRecall(patientId, startDate, endDate);
    }
}*/
