package dummy;

import java.util.Date;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableBatchProcessing
@EnableScheduling
public class AsyncDB2MultipleWriterBatchScheduledDemo {
	
	@Autowired
	private JobLauncher jobLauncher; 
	
	@Autowired
    private Job job;

	public static void main(String[] args) {
		SpringApplication.run(AsyncDB2MultipleWriterBatchScheduledDemo.class, args);
	}

	@Scheduled(cron = "*/5 * * * * *")
	public void perform() throws Exception{
	
		System.out.println("Job Started at :" + new Date());
	
		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
				.toJobParameters();
		
	            JobExecution execution = jobLauncher.run(job, param);
	            System.out.println("Execution status: "+ execution.getStatus());
	    }
	

}
