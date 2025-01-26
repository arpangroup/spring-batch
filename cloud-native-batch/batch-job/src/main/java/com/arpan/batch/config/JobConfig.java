package com.arpan.batch.config;

import com.arpan.batch.batch.DownloadingJobExecutionListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourceRegion;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class JobConfig {
    @Autowired
    private ResourcePatternResolver resourcePatternResolver;

    @Autowired
    private StepBuilder stepBuilder;

    @Autowired
    private JobBuilder jobBuilder;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DownloadingJobExecutionListener downloadingJobExecutionListener() {
        return new DownloadingJobExecutionListener();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader reader(@Value("#{jobExecutionContext['localFiles']}") String paths) throws IOException {
        System.out.println(">> paths = " + paths);
        MultiResourceItemReader<Foo> reader = new MultiResourceItemReader<Foo>();

        reader.setName("multiReader");
        reader.setDelegate(delegate());

        String[] parsedPaths = paths.split(",");
        System.out.println(">> ParsedPaths = " + parsedPaths.length);
        List<Resource> resources = new ArrayList<>(parsedPaths.length);

        for (String parsedPath : parsedPaths) {
            Resource resource = new FileSystemResource(parsedPath);
            System.out.println(">> resource = " + resource.getURI());
            resources.add(resource);
        }

        reader.setResources(resources.toArray(new Resource[resources.size()]));

        return reader;
    }

    @Bean
    public JdbcBatchItemWriter<Foo> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Foo>()
                .dataSource(dataSource)
                .beanMapped()
                .sql("INSERT INTO FOO VALUES (:first, :second, :third, :message)")
                .build();
    }

    @Bean
    public Step step() throws Exception {
        return this.stepBuuilderFactory.get("step")
                .<Foo, Foo>chunk(20)
                .reader(reader(null))
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job job(JobExecutionListener jobExecutionListener) throws Exception {
        return this.jobBuilderFactory.get("s3jdbc")
                .listener(jobExecutionListener)
                .start(step())
                .build();
    }
}
