package org.motechproject.tama.tools.seed;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.motechproject.cmslite.api.model.ResourceQuery;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.tama.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class AudioSeed extends Seed {

    private static final String WAV_FILES_LOCATION = "wav.files.location";

    Logger logger = Logger.getLogger(this.getClass());

    CMSLiteService cmsLiteService;
    private String wavFilesLocation;
    private int poolSize =  10;

    @Autowired
    public AudioSeed(CMSLiteService cmsLiteService, @Qualifier("ivrProperties") Properties ivrProperties) {
        this.cmsLiteService = cmsLiteService;
        this.wavFilesLocation = (String) ivrProperties.get(WAV_FILES_LOCATION);
    }

    @Override
    protected void load() {
        File wavs_dir = new File(wavFilesLocation);
        String[] language_dirs = wavs_dir.list(new AndFileFilter(Arrays.asList(HiddenFileFilter.VISIBLE, DirectoryFileFilter.INSTANCE)));

        if (language_dirs == null) {
            logger.warn("No language specific dirs found in the location - " + wavFilesLocation);
            return;
        }

        for (final String language_dir : language_dirs) {
            String language_dir_path = wavFilesLocation + "/" + language_dir;
            for (String sub_folder : new File(language_dir_path).list(DirectoryFileFilter.INSTANCE)) {
                String subFolderPath = language_dir_path + "/" + sub_folder;
                String[] wav_files = new File(subFolderPath + "/").list(new SuffixFileFilter(".wav"));
                if (wav_files == null) return;
                ExecutorService executor = Executors.newFixedThreadPool(poolSize);
                for (String wav_file : wav_files) {
                    final String wavFilePath = subFolderPath + "/" + wav_file;
                    Runnable worker = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                File wav = new File(wavFilePath);
                                ResourceQuery resourceQuery = new ResourceQuery(FileUtil.sanitizeFilename(wav.getName()), new File(language_dir).getName());
                                FileInputStream inputStream = new FileInputStream(wav);
                                String md5Checksum = new MD5Checksum().getMD5Checksum(wavFilePath);
                                cmsLiteService.addContent(resourceQuery, inputStream, md5Checksum);
                                logger.info("loaded " + wavFilePath);
                                inputStream.close();
                            } catch (Exception e) {
                                logger.error("Could not load wav file : " + wavFilePath, e);
                            }
                        }
                    };
                    executor.execute(worker);
                }
                executor.shutdown();
                while (!executor.isTerminated()) {

                }
            }

        }
    }
}
