package dwayne.shim.gonggochatni.allinone.data.service;

import dwayne.shim.gonggochatni.allinone.data.service.util.IndexPathUtil;
import dwayne.shim.gonggochatni.searching.SearchingExecutor;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.map.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.sql.DataSource;
import java.io.File;

@Log4j2
@SpringBootApplication
@EnableScheduling
@EnableTransactionManagement
@PropertySource("classpath:config/allinone.properties")
public class Application extends SpringBootServletInitializer {


    @Value("${job.original.dir}")
    private String jobOriginalDir;

    @Value("${job.index.path.file}")
    private String jobIndexPathFile;

    @Value("${job.index.dir1}")
    private String jobIndexDir1;

    @Value("${job.index.dir2}")
    private String jobIndexDir2;

    @Value("${task.executor.core.thread.count}")
    private int numCoreThreads;

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(numCoreThreads);
        taskExecutor.setMaxPoolSize(numCoreThreads * 2);
        taskExecutor.setQueueCapacity(numCoreThreads * 10);
        return taskExecutor;
    }

    @Bean
    public IndexPathUtil indexPathUtil() {
        return new IndexPathUtil();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public SearchingExecutor searchingExecutor(IndexPathUtil indexPathUtil) throws Exception {
        return new SearchingExecutor(indexPathUtil.getCurrentIndexPath(jobIndexPathFile, jobIndexDir1));
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties hikariDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource hikariDataSource() {
        return hikariDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public Validator validator() {
        return new LocalValidatorFactoryBean();
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}
