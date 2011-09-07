package org.motechproject.tama.tools.seed;

import org.apache.commons.io.filefilter.*;
import org.apache.log4j.Logger;
import org.motechproject.cmslite.api.CMSLiteService;
import org.motechproject.cmslite.api.ResourceQuery;
import org.motechproject.tama.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;

@Component
public class AudioSeed extends Seed {

    private static final String WAV_FILES_LOCATION = "wav.files.location";

    Logger logger = Logger.getLogger(this.getClass());

    CMSLiteService cmsLiteService;
    private String wavFilesLocation;
    private FileUtil fileUtil;

    @Autowired
    public AudioSeed(CMSLiteService cmsLiteService, @Qualifier("ivrProperties") Properties ivrProperties, FileUtil fileUtil) {
        this.cmsLiteService = cmsLiteService;
        this.wavFilesLocation = (String) ivrProperties.get(WAV_FILES_LOCATION);
        this.fileUtil = fileUtil;
    }

    @Override
    protected void load() {
        File wavs_dir = new File(wavFilesLocation);
        String[] language_dirs = wavs_dir.list(new AndFileFilter(Arrays.asList(HiddenFileFilter.VISIBLE, DirectoryFileFilter.INSTANCE)));

        if (language_dirs == null) {
            logger.warn("No language specific dirs found in the location - " + wavFilesLocation);
            return;
        }

        for (String language_dir : language_dirs) {
            String language_dir_path = wavFilesLocation + "/" + language_dir;
            for (String sub_folder : new File(language_dir_path).list(DirectoryFileFilter.INSTANCE)) {
                String subFolderPath = language_dir_path + "/" + sub_folder;
                String[] wav_files = new File(subFolderPath + "/").list(new SuffixFileFilter(".wav"));
                if (wav_files == null) return;
                for (String wav_file : wav_files) {
                    String wavFilePath = subFolderPath + "/" + wav_file;
                    try {
                        File wav = new File(wavFilePath);
                        ResourceQuery resourceQuery = new ResourceQuery(fileUtil.sanitizeFilename(wav.getName()), new File(language_dir).getName());
                        FileInputStream inputStream = new FileInputStream(wav);
                        cmsLiteService.addContent(resourceQuery, inputStream);
                        logger.info("loaded " + wavFilePath);
                    } catch (Exception e) {
                        logger.error("Could not load wav file : " + wavFilePath, e);
                    }
                }

            }
        }
    }
}
