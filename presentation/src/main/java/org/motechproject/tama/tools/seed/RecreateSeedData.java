package org.motechproject.tama.tools.seed;

import org.motechproject.tama.tools.RecreateDB;

public class RecreateSeedData {
    public static void main(String[] args) throws InterruptedException {
        RecreateDB.main(args);
        SetupSeedData.main(args);
        if (System.getProperty("exit.at.end") != null) System.exit(0);
    }
}
