package com.arpan.__batch_basic.p2_tasklet_with_listener;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class JobConfig2 {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobCompletionNotificationListener jobCompletionNotificationListener;
    private final JobCompletionNotificationListenerWithMicrometer jobCompletionNotificationListenerWithMicrometer;

    @Bean
    public Job firstJob() { // Step1: Creating the Job
        return new JobBuilder("Job2_with_tasklet_execution_listener", jobRepository)
                .listener(jobCompletionNotificationListener)
                .listener(jobCompletionNotificationListenerWithMicrometer)
                .start(firstStep())
                .build();
    }

    private Step firstStep() { // Step2: Creating the Step which is responsible to execute task (i.e. tasklet)
        return new StepBuilder("first-step", jobRepository)
                .tasklet(new CustomStepExecutionListener(), transactionManager)
                .build();
    }
}
