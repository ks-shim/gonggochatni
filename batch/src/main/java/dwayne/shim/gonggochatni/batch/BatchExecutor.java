package dwayne.shim.gonggochatni.batch;

import dwayne.shim.gonggochatni.core.keyword.KeywordExtractor;
import dwayne.shim.gonggochatni.crawler.JobCrawler;
import dwayne.shim.gonggochatni.crawler.apicaller.ApiCaller;
import dwayne.shim.gonggochatni.crawler.apicaller.DefaultApiCaller;
import dwayne.shim.gonggochatni.indexing.IndexingExecutor;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class BatchExecutor {

    public void executeBatchSteps(Properties prop) throws Exception {

        String jobDataDir = prop.getProperty("job.original.dir");
        int daysBefore = Integer.parseInt(prop.getProperty("crawl.days.before"));

        boolean enableCrawling = Boolean.parseBoolean(prop.getProperty("crawl.enable"));
        if(enableCrawling) {
            System.out.println("\n\nStart executing step-1 ...");
            executeBatchStep1(jobDataDir, daysBefore);
            System.out.println("End executing step-1 ...");
        } else
            System.out.println("\n\nSkipped executing step-1 ...");

        //-------------------------------------------------------------------------------
        // 2. get to-be index dir path ...
        //-------------------------------------------------------------------------------
        System.out.println("\n\nStart getting to-be index path ...");
        String url = prop.getProperty("index.rest.to-be-path");
        String toBePath = "";
        try (ApiCaller apiCaller = new DefaultApiCaller()) {
            toBePath = apiCaller.callAsGet(url);
            if(StringUtils.isBlank(toBePath)) throw new RuntimeException("Got empty TO-BE-PATH from data-service.");
            System.out.println("TO-BE path : " + toBePath);
        }
        System.out.println("End getting to-be index path ...");

        //-------------------------------------------------------------------------------
        // 3. do indexing dir path ...
        //-------------------------------------------------------------------------------
        String keyExtractorConfigPath = prop.getProperty("key-extractor.config.path");
        boolean enableIndexing = Boolean.parseBoolean(prop.getProperty("indexing.enable"));
        if(enableIndexing) {
            System.out.println("\n\nStart executing step-2 ...");
            executeBatchStep2(jobDataDir, toBePath, keyExtractorConfigPath);
            System.out.println("End executing step-2 ...");
        } else
            System.out.println("Skipped executing step-2 ...");

        //-------------------------------------------------------------------------------
        // 4. force data-service to switch index path ...
        //-------------------------------------------------------------------------------
        System.out.println("\n\nStart forcing to switch index path ...");
        url = prop.getProperty("index.rest.switch-path");
        try (ApiCaller apiCaller = new DefaultApiCaller()) {
            apiCaller.callAsPut(url);
        }
        System.out.println("End forcing to switch index path ...");
    }

    private void executeBatchStep1(String jobDataDir,
                                  int daysBefore) throws Exception {
        JobCrawler crawler = new JobCrawler();
        crawler.execute(new File(jobDataDir), daysBefore);
    }

    private void executeBatchStep2(String inDir,
                                  String outDir,
                                  String keyExtractorConfigPath) throws Exception {
        final int docSizeLimit = 1000;
        new IndexingExecutor(new KeywordExtractor(keyExtractorConfigPath)).execute(inDir, docSizeLimit, outDir);
    }


    public void executeIncrementalSteps(Properties prop) throws Exception {
        int hoursBefore = Integer.parseInt(prop.getProperty("crawl.hours.before"));
    }

    public static void main(String[] args) throws Exception {

        if(args.length < 2) {
            System.err.println("Usage : java BatchExecutor <batch:true, incremental:false> <property file path>");
            System.exit(1);
        }

        //-------------------------------------------------------------------------------
        // 1. read properties file ...
        //-------------------------------------------------------------------------------
        System.out.println("Start reading properties ...");
        boolean isBatch = Boolean.parseBoolean(args[0].trim());
        String propFilePath = args[1].trim();
        Properties prop = new Properties();
        try (InputStream in = new FileInputStream(propFilePath)){
            prop.load(in);
        }
        System.out.println("End reading properties ...");

        BatchExecutor executor = new BatchExecutor();
        if(isBatch) executor.executeBatchSteps(prop);
        else executor.executeIncrementalSteps(prop);
    }
}
