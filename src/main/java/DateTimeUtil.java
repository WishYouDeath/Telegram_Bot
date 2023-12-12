import JSON.Example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private static final Logger logger = LogManager.getLogger(Parser.class);
    public static String getDate(Example example) {
        String startTime = example.getStartAt();
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startTime, inputFormatter);
            LocalDateTime adjustedDateTime = startDateTime.plusHours(5);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
            return adjustedDateTime.format(outputFormatter);
        } catch (Exception e) {
            logger.error("Произошла ошибка при обработке даты: {}", e.getMessage());
            throw new ExceptionHandler.DataProcessingException("Произошла ошибка при обработке даты", e);
        }
    }
}