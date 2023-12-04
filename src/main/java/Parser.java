import JSON.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.LocalDate;

public class Parser {
    //public static Map<String, String> cache = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Parser.class);

    public static boolean isTeamMatch(String teamName, Example match) {
        return isTeamNameMatch(teamName, match.getHomeTeam(), match.getAwayTeam(), "ru") ||
                isTeamNameMatch(teamName, match.getHomeTeam(), match.getAwayTeam(), "en");
    }
    public int compareCategory(String category) {
        switch (category.toLowerCase()) {
            case "футбол":
                return 1;
            case "теннис":
                return 2;
            case "баскетбол":
                return 3;
            case "хоккей":
                return 4;
            case "волейбол":
                return 5;
            case "гандбол":
                return 6;
            default:
                return 0;
        }
    }
    public static boolean isTeamNameMatch(String teamName, HomeTeam homeTeam, AwayTeam awayTeam, String language) {
        if ((homeTeam.getNameTranslations() != null && homeTeam.getNameTranslations().containsKey(language)) ||
                (awayTeam.getNameTranslations() != null && awayTeam.getNameTranslations().containsKey(language))){
            String translatedHomeName = homeTeam.getNameTranslations().get(language)!=null ? homeTeam.getNameTranslations().get(language): "";
            String translatedAwayName = awayTeam.getNameTranslations().get(language)!=null ? awayTeam.getNameTranslations().get(language): "";

            return (translatedAwayName.equalsIgnoreCase(teamName) || translatedHomeName.equalsIgnoreCase(teamName));
        }
        return false;
    }

    public String receiveData(String teamName, String category) {
        /*if (cache.containsKey(teamName.toLowerCase())) {
            return cache.get(teamName.toLowerCase());
        }*/
        int numberOfSport = compareCategory(category);
        try {
            String baseUrl = "https://sportscore1.p.rapidapi.com/sports/" +numberOfSport + "/events/date/";
            //LocalDate currentDate = LocalDate.now() - наша дата UTC+5
            LocalDate currentDate = LocalDate.now(Clock.systemUTC());// Получение даты сайта (-5 часов)
            // Преобразование даты в строку с форматом "yyyy-MM-dd"
            String data = currentDate.toString();
            String urlString = baseUrl + data;

            String responseBody = APIRequest.sendGETRequest(urlString);

            if (responseBody.isEmpty()) {
                logger.error("Произошла ошибка при обработке данных с сайта: получен пустой ответ");
                throw new ExceptionHandler.EmptyResponseException("Произошла ошибка при обработке данных с сайта");
            }

            try {
                StringBuilder matchInfoBuilder = new StringBuilder();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode matchesNode = rootNode.get("data");
                for (JsonNode matchNode : matchesNode) {
                    Example match = objectMapper.treeToValue(matchNode, Example.class);
                    if (isTeamMatch(teamName, match)) {
                        matchInfoBuilder.append(MatchDataUtil.processMatchData(match,category));
                        return matchInfoBuilder.toString();
                    }
                }
                matchInfoBuilder.append("Такого матча сегодня нет");
               // cache.put(teamName.toLowerCase(), matchInfoBuilder.toString());
                return matchInfoBuilder.toString();

            } catch (JsonProcessingException e) {
                logger.error("Произошла ошибка при обработке данных с сайта", e);
                throw new ExceptionHandler.DataProcessingException("Произошла ошибка при обработке данных с сайта", e);
            }

        } catch (Exception e) {
            logger.error("Произошла ошибка при отправке HTTP-запроса", e);
            throw new ExceptionHandler.HttpRequestException("Произошла ошибка при отправке HTTP-запроса", e);
        }
    }
}


