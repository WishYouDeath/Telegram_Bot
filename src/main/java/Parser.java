import JSON.Home_team;
import JSON.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.time.LocalDate;

public class Parser {
    //public static Map<String, String> cache = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Parser.class);
    public static boolean isTeamMatch(String teamName, Match match){
        String homeTeam = match.getHomeTeam().getName();
        String awayTeam = match.getAwayTeam().getName();
        if (isTeamNameMatch(teamName, match, "ru", homeTeam, awayTeam)) {
            return true;
        }
        return isTeamNameMatch(teamName, match, "en", homeTeam, awayTeam);
    }

    public static boolean isTeamNameMatch(String teamName, Match match, String language, String homeTeam, String awayTeam) {
        if (homeTeam != null && match.getHomeTeam().getNameTranslations() != null && match.getHomeTeam().getNameTranslations().containsKey(language)) {
            String translatedName = match.getHomeTeam().getNameTranslations().get(language);
            return translatedName.equalsIgnoreCase(teamName);
        }
        if (awayTeam != null && match.getAwayTeam().getNameTranslations() != null && match.getAwayTeam().getNameTranslations().containsKey(language)) {
            String translatedName = match.getAwayTeam().getNameTranslations().get(language);
            return translatedName.equalsIgnoreCase(teamName);
        }
        return false;
    }

    public String receiveData(String teamName) {
        /*if (cache.containsKey(teamName.toLowerCase())) {
            return cache.get(teamName.toLowerCase());
        }*/

        try {
            String baseUrl = "https://sportscore1.p.rapidapi.com/sports/1/events/date/";
            // Получение текущей даты
            LocalDate currentDate = LocalDate.now();
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
                Match match = objectMapper.convertValue(responseBody, Match.class);
                JsonNode rootNode = objectMapper.readTree(responseBody);
                JsonNode matchesNode = rootNode.get("data");

                for (JsonNode matchNode : matchesNode) {
                    if (isTeamMatch(teamName, match)) {
                        matchInfoBuilder.append(MatchDataUtil.processMatchData(matchNode));
                        //cache.put(teamName.toLowerCase(), matchInfoBuilder.toString());
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


