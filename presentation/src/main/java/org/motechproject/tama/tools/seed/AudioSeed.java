package org.motechproject.tama.tools.seed;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.tama.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AudioSeed extends Seed {

    private static final String WAV_FILES_LOCATION = "wav.files.location";

    Logger logger = Logger.getLogger(this.getClass());

    CMSLiteService cmsLiteService;
    private String wavFilesLocation;
    private int poolSize = 10;

    @Autowired
    public AudioSeed(CMSLiteService cmsLiteService, @Qualifier("ivrProperties") Properties ivrProperties) {
        this.cmsLiteService = cmsLiteService;
        this.wavFilesLocation = (String) ivrProperties.get(WAV_FILES_LOCATION);
    }

    @Override
    protected void load() {
        File wavsDir = new File(wavFilesLocation);
        String[] languageDirs = wavsDir.list(new AndFileFilter(Arrays.asList(HiddenFileFilter.VISIBLE, DirectoryFileFilter.INSTANCE)));

        if (languageDirs == null) {
            logger.warn("No language specific dirs found in the location - " + wavFilesLocation);
            return;
        }

        for (final String languageDir : languageDirs) {
            String languageDirPath = wavFilesLocation + "/" + languageDir;
            for (String subFolder : new File(languageDirPath).list(DirectoryFileFilter.INSTANCE)) {
                String subFolderPath = languageDirPath + "/" + subFolder;
                String[] wavFiles = new File(subFolderPath + "/").list(new SuffixFileFilter(".wav"));
                if (wavFiles == null) return;
                ExecutorService executor = Executors.newFixedThreadPool(poolSize);
                List<Callable<Object>> callableList = new ArrayList();
                for (String wavFile : wavFiles) {
                    final String wavFilePath = subFolderPath + "/" + wavFile;
                    callableList.add(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            try {
                                File wav = new File(wavFilePath);
                                String language = new File(languageDir).getName();
                                String name = FileUtil.sanitizeFilename(wav.getName());
                                FileInputStream inputStream = new FileInputStream(wav);
                                String md5Checksum = new MD5Checksum().getMD5Checksum(wavFilePath);
                                cmsLiteService.addContent(new StreamContent(language, name, inputStream, md5Checksum, "audio/x-wav"));
                                logger.info("loaded " + wavFilePath);
                                inputStream.close();
                            } catch (Exception e) {
                                logger.error("Could not load wav file : " + wavFilePath, e);
                            }
                            return null;
                        }
                    });
                }
                try {
                    executor.invokeAll(callableList);
                } catch (InterruptedException e) {
                    logger.error("Exception loading audio seed");
                }
            }

        }
    }
}
