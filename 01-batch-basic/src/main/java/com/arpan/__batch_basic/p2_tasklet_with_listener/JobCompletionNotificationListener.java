package com.arpan.__batch_basic.p2_tasklet_with_listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

@Slf4j
public class JobCompletionNotificationListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("inside beforeJob................");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("inside afterJob................");
        log.info("JOB_NAME: {}", jobExecution.getJobInstance().getJobName());
        log.info("JOB_ID: {}", jobExecution.getJobId());
        log.info("JOB_PARAMETERS: {}", jobExecution.getJobParameters());
        log.info("END_TIME: {}", jobExecution.getEndTime());
        log.info("EXIT_STATUS: {}", jobExecution.getExitStatus());
        log.info("STATUS: {}", jobExecution.getStatus());

        // submitJobStatusToMicrometer();
        // generateJobStatusOutput();


    }

    /*private void submitJobStatusToMicrometer(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            completedJobsCounter.increment();
        } else {
            failedJobsCounter.increment();
        }
    }*/

    /*private void generateJobStatusOutput(JobExecution jobExecution) {
        JobParameter value = jobExecution.getJobParameters().getParameters().get("input");
        String fileContent = "\r\n" + value + " " + jobExecution.getEndTime();
        try (FileOutputStream outputStream = new FileOutputStream("output_metadata.txt", true)){
            byte[] strToBytes = fileContent.getBytes();
            outputStream.write(strToBytes);
        } catch (IOException ex) {
            log.error("Error occurred while creating metadata file: {}", ex.getMessage());
        }
    }*/
}
