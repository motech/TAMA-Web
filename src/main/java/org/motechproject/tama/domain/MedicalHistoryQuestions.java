package org.motechproject.tama.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MedicalHistoryQuestions {

  public static MedicalHistoryQuestion pregnancy() {
      return new MedicalHistoryQuestion("Pregnant",true);
  }

  public static MedicalHistoryQuestion baseLinePretherapy() {
      return new MedicalHistoryQuestion("Was Baseline pre-therapy Hb was lower than 10",false);
  }

  public static List<MedicalHistoryQuestion> all() {
      return Arrays.asList(pregnancy(), baseLinePretherapy());
  }
}


