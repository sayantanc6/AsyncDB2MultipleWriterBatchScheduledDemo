package dummy.config;

import java.util.Arrays;
import java.util.Collections;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import dummy.Book;
import dummy.BookMapper;
import dummy.JobCompletionListener;
import dummy.MyExcelWriter;
import dummy.StringHeaderWriter;

@Configuration
public class BatchConfiguration { 
	
    private static final String PROPERTY_CSV_EXPORT_FILE_HEADER = "batch.job.export.file.header";
    private static final String PROPERTY_CSV_EXPORT_FILE_PATH = "file.output";
    
    @Autowired
	  Environment environment;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Bean
	public Job readDb() throws Exception {
		return jobBuilderFactory.get("readDb").incrementer(new RunIdIncrementer())
				.listener(listener())
				.flow(step1()).end().build(); 
	}
	
	@Bean
	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
				.<Book,Book>chunk(1000)
				.reader(Reader(null))
				.processor(Processor())
				.writer(compositeparallelwriter())
				.build();
	}
	
	@Bean
	public CompositeItemWriter<Book> compositeparallelwriter() throws Exception {
		CompositeItemWriter<Book> comp_writer = new CompositeItemWriter<>();
		comp_writer.setDelegates(Arrays.asList(xmlWriter(),csvwriter(),excelWriter()));
		return comp_writer;
	}
	
	@Bean
	public MyExcelWriter excelWriter() {
		return new MyExcelWriter();
	}
	
	@Bean
    public ItemProcessor<Book, Book> Processor() { 
        return (transaction) -> { 
            Thread.sleep(1);
            return transaction;
        };
    }
	
	@Bean
    public ItemReader<Book> Reader(DataSource dataSource) {

        return new JdbcPagingItemReaderBuilder<Book>()
                .name("Reader")
                .dataSource(dataSource)
                .selectClause("SELECT * ")
                .fromClause("FROM BOOK ")
                .whereClause("WHERE ISBN <= 1000000 ")
                .sortKeys(Collections.singletonMap("ISBN", Order.ASCENDING))
                .rowMapper(new BookMapper())
                .build();
    }
	
	@Bean
	  public StaxEventItemWriter<Book> xmlWriter() throws Exception {
	    StaxEventItemWriter<Book> writer = new StaxEventItemWriter<>();
	    writer.setResource(new FileSystemResource("books.xml")); 
	    writer.setMarshaller(studentUnmarshaller());
	    writer.setRootTagName("books");
	    return writer;
		
	  }
	 
	@Bean
	  public Jaxb2Marshaller studentUnmarshaller() {
		  Jaxb2Marshaller unMarshaller = new Jaxb2Marshaller();
	    unMarshaller.setClassesToBeBound(Book.class); 
	    return unMarshaller;
	  }
	
	@Bean
	public JobExecutionListener listener() {
		return new JobCompletionListener();
	}

	@Bean
	  public FlatFileItemWriter<Book> csvwriter() throws Exception{ 
		  String exportFilePath = environment.getRequiredProperty(PROPERTY_CSV_EXPORT_FILE_PATH); 
	        Resource exportFileResource = new FileSystemResource(exportFilePath);
	        
		  String exportFileHeader = environment.getRequiredProperty(PROPERTY_CSV_EXPORT_FILE_HEADER);
	        StringHeaderWriter headerWriter = new StringHeaderWriter(exportFileHeader);
	        
		  FlatFileItemWriter<Book> writer = new FlatFileItemWriter<>();
		  writer.setHeaderCallback(headerWriter); 
		  writer.setResource(exportFileResource);
		  writer.setAppendAllowed(true);
		  writer.setLineAggregator(new DelimitedLineAggregator<Book>() {
	            {
	                setDelimiter(",");
	                setFieldExtractor(new BeanWrapperFieldExtractor<Book>() {
	                    {
	                        setNames(new String[] {"author","ISBN", "title", "price"});
	                    }
	                });
	            }
	        });
		  return writer;
	  }
}

