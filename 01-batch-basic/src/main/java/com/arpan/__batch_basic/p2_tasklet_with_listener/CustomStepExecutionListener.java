package com.arpan.__batch_basic.p2_tasklet_with_listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class CustomStepExecutionListener implements Tasklet, StepExecutionListener {
    public CustomStepExecutionListener() {
        System.out.println("demo tasklet constructor");
    }

    @Override
    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
        System.out.println("executing the task using tasklet...........");
        return RepeatStatus.FINISHED;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        System.out.println("inside beforeStep......");
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("inside afterStep................");
        log.info("STEP_NAME      : {}",  stepExecution.getStepName());
        log.info("STEP_NAME      : {}",  stepExecution.getStepName());
        log.info("EXIT_STATUS    : {}",  stepExecution.getExitStatus());
        log.info("READ_COUNT     : {}",  stepExecution.getReadCount());
        log.info("WRITE_COUNT    : {}",  stepExecution.getWriteCount());
        log.info("COMMIT_COUNT   : {}",  stepExecution.getCommitCount());
        log.info("ROLLBACK_COUNT : {}",  stepExecution.getRollbackCount());
        log.info("START_TIME     : {}",  stepExecution.getStartTime());
        log.info("END_TIME       : {}",  stepExecution.getEndTime());
        log.info("................................");
        return stepExecution.getExitStatus();
    }
}
