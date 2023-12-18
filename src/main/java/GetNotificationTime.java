import JSON.Example;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class GetNotificationTime {
    private static final Logger logger = LogManager.getLogger(Parser.class);
    public static String getTimeForNotification(Message message, String teamName, Map<Long, String> userSelectedCategoryMap, Map<Long, String> userSelectedDateMap) {
        Parser parsing = new Parser();
        String selectedDate = userSelectedDateMap.get(message.getChatId());
        String category = userSelectedCategoryMap.get(message.getChatId());
        return (parsing.receiveData(teamName, category, selectedDate));
    }

    private static String getDate(Example example, String status, String category) {
        switch (status) {
            case "finished":
                return MatchDataUtil.processMatchData(example, category);
            case "notstarted":
                try {
                    String startTime = example.getStartAt();
                    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime startDateTime = LocalDateTime.parse(startTime, inputFormatter);
                    LocalDateTime adjustedDateTime = startDateTime.plusHours(5);
                    LocalDateTime currentTime = LocalDateTime.now();
                    LocalDateTime fiveMinutesLater = adjustedDateTime.plusMinutes(5);
                    if (fiveMinutesLater.isBefore(currentTime)) {
                        return MatchDataUtil.processMatchData(example, category);
                    }
                    else{
                        return "Время матча не наступило";
                    }
                } catch (Exception e) {
                    // Обработка исключения
                    logger.error("Произошла ошибка при обработке даты: {}", e.getMessage());
                    throw new ExceptionHandler.DataProcessingException("Произошла ошибка при обработке даты", e);
                }
        }
        return "Что-то пошло не так";
    }
    public static String ParseDate(Example example, Map<Long, String> userSelectedCategoryMap, Message message) {
        String status = example.getStatus() != null ? example.getStatus() : "Н/Д";
        switch (status.toLowerCase()) {
            case "finished":
            case "notstarted":
                return (getDate(example, status.toLowerCase(), userSelectedCategoryMap.get(message.getChatId())));
            case "inprogress":
                return "Матч уже начался!";
            case "postponed":
                return "Матч был отложен";
            default:
                return "Неизвестный статус матча";
        }
    }

}
