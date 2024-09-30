package TimeTracker.TimeTracker;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TimeTrackerApplication {
	private static final Logger logger = LoggerFactory.getLogger(TimeTrackerApplication.class);

	public static void main(String[] args) {
		logger.info("Before Start program");
		SpringApplication.run(TimeTrackerApplication.class, args);
		logger.info("Start program");

		//logger.info("Example log from {}", TimeTrackerApplication.class.getSimpleName());

	}

}
