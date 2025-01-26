package com.arpan.batch.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * This class downloads the file from S3
 */
public class DownloadingJobExecutionListener implements JobExecutionListener {

    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Value("${job.resource-path}")
    private String path;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try {
            Resource[] resources = this.resourcePatternResolver.getResources(this.path);
            StringBuilder paths = new StringBuilder();
            for (Resource resource : resources) {
                File file = File.createTempFile("input", ".csv");
                StreamUtils.copy(resource.getInputStream(), new FileOutputStream(file));

                paths.append(file.getAbsoluteFile() + ", ");
                System.out.println(">>>Downloading file: " + file.getAbsolutePath());
            }
            jobExecution.getExcecutionContext().put("localFiles", paths.substring(0, paths.length() - 1));
        } catch (IOException e) {
            e.printStackTrace();
        }


        JobExecutionListener.super.beforeJob(jobExecution);
    }
}
