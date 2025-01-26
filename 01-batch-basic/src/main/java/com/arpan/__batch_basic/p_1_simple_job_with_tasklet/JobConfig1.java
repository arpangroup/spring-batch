package com.arpan.__batch_basic.p_1_simple_job_with_tasklet;


import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchDataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Profile("local")
@Configuration
@RequiredArgsConstructor
@BatchDataSource
public class JobConfig1 {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

//    @Bean
//    public DataSource batchDataSource() {
//        return new EmbeddedDatabaseBuilder()
//                .setType(EmbeddedDatabaseType.HSQL)
//                .addScript("/org/springframework/batch/core/schema-hsqldb.sql")
//                .generateUniqueName(true)
//                .build();
//    }
//    @Bean
//    public JdbcTransactionManager batchTransactionManager(DataSource dataSource) {
//        return new JdbcTransactionManager(dataSource);
//    }

//    @Bean
//    public PlatformTransactionManager transactionManager() {
//        return new ResourcelessTransactionManager();
//    }

//    @Bean
//    public JobRepository jobRepository() {
//        return new
//    }


    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(null);
        factory.setDatabaseType("db2");
        factory.setTransactionManager(transactionManager);
        return factory.getObject();
    }


    @Bean
    public Job firstJob() { // Step1: Creating the Job
        return new JobBuilder("job1", jobRepository)
                .start(firstStep())
                //.listener(JobExecutionListener)
                //.next(step2)
                //.incrementer()
                //.meterRegistry()
                //.validator()
                //.observationRegistry()
                .build();
    }

    private Step firstStep() { // Step2: Creating the Step which is responsible to execute task (i.e. tasklet)
        return new StepBuilder("first-step", jobRepository)
                .tasklet(firstTask(), transactionManager)
                //.listener(StepExecutionListener)
                //.stream(ItemStream)
                .build();
    }

    private Tasklet firstTask() { // Step3: Business logic should be execute using tasklet
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                System.out.println("executing the first Tasklet........");
                return RepeatStatus.FINISHED;
            }
        };
    }
}
