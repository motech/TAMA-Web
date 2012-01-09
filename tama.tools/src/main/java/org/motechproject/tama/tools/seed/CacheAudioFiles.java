package org.motechproject.tama.tools.seed;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.motechproject.tama.common.util.FileUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CacheAudioFiles {
    private static final String WAV_FILES_LOCATION = "wav.files.location";
    private static final String APPLICATION_CONTEXT_XML = "applicationToolsContext.xml";

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

        invokeKooKooURL(String.format("http://kookoo.in/restkookoo/index.php/api/cache/audio/api_key/%s/delete/all", API_KEY));
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
                                String wavFileURL = String.format("%s/%s/%s", CONTENT_LOCATION_URL, language, wavFileName);
                                invokeKooKooURL(String.format("http://kookoo.in/restkookoo/index.php/api/cache/audio?api_key=%s&url=%s", API_KEY, wavFileURL));

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

    private static void invokeKooKooURL(String url) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(new HttpGet(url));
            bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            for (String line; (line = bufferedReader.readLine()) != null; ) {
                logger.info(line);
                System.out.println(line);
            }
        } finally {
            if (bufferedReader != null) bufferedReader.close();
        }
    }
}
