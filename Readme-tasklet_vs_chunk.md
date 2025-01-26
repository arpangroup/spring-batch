# Tasklet vs Chunk
Spring Batch provides two different ways for implementing a job: using `tasklets` and `chunks`.

## 1. Tasklets Approach:
Tasklets are meant to perform a single task within a step. Our job will consist of several steps that execute one after the other. **Each step should perform only one defined task**.

### 1.1. Example: Create a Simple Job using Tasklet
A **Tasklet** is a simple interface that performs a single task within a batch job. It is an alternative to the chunk-based processing model
````java
@Configuration
public class TaskletsConfig {
  @Bean
  // Step1: Creating the Job
  public Job firstJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new JobBuilder("taskletsJob", jobRepository)
            .start(readLinesStep(jobRepository, transactionManager)) // step1
            //.listener(JobExecutionListener)
            .next(processLines(jobRepository, transactionManager)) // step2
            .next(writeLines(jobRepository, transactionManager)) // step3
            //.incrementer()
            //.meterRegistry()
            //.validator()
            //.observationRegistry()
            .build();
  }

  // Step2: Creating the Step which is responsible to execute task (i.e. tasklet)
  private Step readLines(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("readLines", jobRepository)
            .tasklet(lineReaderTasklet(), transactionManager)
            //.listener(StepExecutionListener)
            //.stream(ItemStream)
            .build();
  }

  @Bean
  protected Step processLines(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("processLines", jobRepository)
            .tasklet(linesProcessor(), transactionManager)
            .build();
  }

  @Bean
  protected Step writeLines(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new StepBuilder("writeLines", jobRepository)
            .tasklet(linesWriter(), transactionManager)
            .build();
  }

  // Step3: Business logic should be execute using tasklet
  private Tasklet lineReaderTasklet() {
    return new Tasklet() {
      @Override
      public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("executing the first Tasklet........");
        return RepeatStatus.FINISHED;
      }
    };
  }
    
}
````
This means that our `“taskletsJob”` will consist of three steps. The first one (**readLines**) will execute the tasklet defined in the bean linesReader and move to the next step: **processLines**. ProcessLines will perform the tasklet defined in the bean linesProcessor and go to the final step: writeLines.


Here’s an example of a simple Tasklet that logs a message:

````java
@Component
public class LinesReader implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        System.out.println("Executing MyTasklet...");
        // Perform your task logic here
        return RepeatStatus.FINISHED; // Indicates the task is complete
    }
}

@Component
public class LinesProcessor  implements Tasklet {
  ....
}

@Component
public class LinesWriter  implements Tasklet {
  ....
}
````

## 1.2. Running the Job (Test)
````java
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = TaskletsConfig.class)
public class TaskletsIntegrationTest {

    @Autowired 
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenTaskletsJob_whenJobEnds_thenStatusCompleted()
      throws Exception {
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
    
    // ######################################################################## //
    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
      return new JobLauncherTestUtils();
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
      JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
      factory.setDataSource(dataSource());
      factory.setTransactionManager(transactionManager());
      return factory.getObject();
    }

    @Bean
    public DataSource dataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("org.sqlite.JDBC");
      dataSource.setUrl("jdbc:sqlite:repository.sqlite");
      return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
      return new ResourcelessTransactionManager();
    }

    @Bean
    public JobLauncher jobLauncher() throws Exception {
      SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
      jobLauncher.setJobRepository(jobRepository());
      return jobLauncher;
    }  
}
````
ContextConfiguration annotation is pointing to the Spring context configuration class, that has our job definition.


## 2. Chunks Approach
Instead of reading, processing and writing all the lines at once, it’ll read, process and write a fixed amount of records (chunk) at a time.

As a result, the flow will be slightly different:
1. While there’re lines:
    - Do for X amount of lines:
        - Read one line
        - Process one line
    - Write X amount of lines.

### 2.1. Example: Create a Simple Job using Chunk
````java
@Configuration
public class ChunksConfig {

  @Bean
  public Job job(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
    return new JobBuilder("chunksJob", jobRepository)
            .start(step1())
            .build();
  }

  @Bean
  protected Step step1() {
    return new StepBuilder("processLines", jobRepository)
            .<SalesInfoDTO, SalesInfoEntity> chunk(2, transactionManager) // read, process and write two lines at a time.
            .reader(reader)
            .processor(processor)
            .writer(writer)
            .build();
  }

   @Bean
   public FlatFileItemReader<SalesInfoDTO> salesInfoFileReader(){
      return new FlatFileItemReaderBuilder<SalesInfoDTO>()
              .resource(new ClassPathResource("input.csv"))
              .name("salesInfoFileReader")
              .delimited()
              .delimiter(",")
              .names(new String[]{"product","seller","sellerId","price","city","category"})
              .linesToSkip(1)
              .targetType(SalesInfoDTO.class)
              .build();
   }



   public class SalesInfoItemProcessor implements ItemProcessor<SalesInfoDTO, SalesInfoEntity> {
      @Override
      public ProductEntity process(SalesInfoDTO item) throws Exception {
         log.info("processing the item: {}",item.toString());
         return mapper.mapToEntity(item);
      }
   }

   @Bean
   public JpaItemWriter<ProductEntity> salesInfoItemWriter(){
      return new JpaItemWriterBuilder<SalesInfo>()
              .entityManagerFactory(entityManagerFactory)
              .build();
   }
}
````
In this case, there’s only one step performing only one tasklet.

### 2.2. Running the Job (Test)
````java
@SpringBatchTest
@EnableAutoConfiguration
@ContextConfiguration(classes = ChunksConfig.class)
public class ChunksIntegrationTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void givenChunksJob_whenJobEnds_thenStatusCompleted() 
      throws Exception {
 
        JobExecution jobExecution = jobLauncherTestUtils.launchJob();
 
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus()); 
    }
}
````



## Resources:
- [Tasklets vs Chunks](https://www.baeldung.com/spring-batch-tasklet-chunk)