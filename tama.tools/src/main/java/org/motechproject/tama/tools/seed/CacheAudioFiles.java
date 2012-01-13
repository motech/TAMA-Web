package org.motechproject.tama.tools.seed;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.motechproject.tama.common.util.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheAudioFiles {
    private static final String WAV_FILES_LOCATION = "wav.files.location";
    private static final String APPLICATION_CONTEXT_XML = "applicationSeedDataContext.xml";

    static Logger logger = Logger.getLogger(CacheAudioFiles.class);

    private static String wavFilesLocation;
    private static int poolSize = 10;
    private static String API_KEY;
    private static String CONTENT_LOCATION_URL;

    public static void main(String[] args) throws InterruptedException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext(APPLICATION_CONTEXT_XML);
        Properties seedProperties = (Properties) context.getBean("seedProperties");
        wavFilesLocation = (String) seedProperties.get(WAV_FILES_LOCATION);
        API_KEY = args[0];
        CONTENT_LOCATION_URL = args[1];
        cacheFiles();
    }

    private static void cacheFiles() throws IOException {
        File wavsDir = new File(wavFilesLocation);
        String[] languageDirs = wavsDir.list(new AndFileFilter(Arrays.asList(HiddenFileFilter.VISIBLE, DirectoryFileFilter.INSTANCE)));

        if (languageDirs == null) {
            logger.warn("No language specific dirs found in the location - " + wavFilesLocation);
            return;
        }

        deleteKooKooCache(String.format("http://kookoo.in/restkookoo/index.php/api/cache/audio/api_key/%s/delete/all", API_KEY));
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
                                String wavFileName = FileUtil.sanitizeFilename(wav.getName());
                                String wavFileURL = String.format("%s%s/%s", CONTENT_LOCATION_URL, language, wavFileName);
                                insertIntoKooKooCache("http://kookoo.in/restkookoo/index.php/api/cache/audio/", wavFileURL);
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
                    logger.error("Exception caching audio seed");
                }
            }
        }
    }

    private static void deleteKooKooCache(String url) throws IOException {
        HttpClient httpClient = new HttpClient();
        int responseCode = httpClient.executeMethod(new GetMethod(url));
        if(responseCode >= 200 && responseCode < 300) {
            logger.info("Successfully deleted cache");
            System.out.println("Successfully deleted cache");
        }
    }

    private static void insertIntoKooKooCache(String url, String wavFileURL) throws IOException {
        HttpClient httpClient = new HttpClient();
        PostMethod postMethod = new PostMethod(url);
        postMethod.addParameter("api_key", API_KEY);
        postMethod.addParameter("url", URLEncoder.encode(wavFileURL, "UTF-8"));
        int responseCode = httpClient.executeMethod(postMethod);
        if(responseCode >= 200 && responseCode < 300) {
            logger.info("Successfully cached : " + wavFileURL);
            System.out.println("Successfully cached : " + wavFileURL);
        } else {
            logger.info("Caching of "+ wavFileURL +" failed with " + responseCode);
            System.out.println("Caching of "+ wavFileURL +" failed with " + responseCode);
        }
    }
}
