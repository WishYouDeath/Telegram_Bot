import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class Parser {
    public static Map<String, String> cache = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Parser.class);
    public static boolean isTeamMatch(String teamName, JsonNode matchNode) {
        JsonNode homeTeamNode = matchNode.get("home_team");
        JsonNode awayTeamNode = matchNode.get("away_team");
        if (isTeamNameMatch(teamName, homeTeamNode, "ru") || isTeamNameMatch(teamName, awayTeamNode, "ru")) {
            return true;
        }
        return isTeamNameMatch(teamName, homeTeamNode, "en") || isTeamNameMatch(teamName, awayTeamNode, "en");
    }

    public static boolean isTeamNameMatch(String teamName, JsonNode teamNode, String language) {
        if (teamNode != null && teamNode.has("name_translations") && teamNode.get("name_translations").has(language)) {
            String translatedName = teamNode.get("name_translations").get(language).asText();
            return translatedName.equalsIgnoreCase(teamName);
        }
        return false;
    }

    public String receiveData(String teamName) {
        if (cache.containsKey(teamName.toLowerCase())) {
            return cache.get(teamName.toLowerCase());
        }

        try {
            String baseUrl = "https://sportscore1.p.rapidapi.com/sports/1/events/date/";
            String data = "2023-10-26";
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
                    if (isTeamMatch(teamName, matchNode)) {
                        matchInfoBuilder.append(MatchDataUtil.processMatchData(matchNode));
                        cache.put(teamName.toLowerCase(), matchInfoBuilder.toString());
                        return matchInfoBuilder.toString();
                    }
                }

                matchInfoBuilder.append("Такого матча сегодня нет");
                cache.put(teamName.toLowerCase(), matchInfoBuilder.toString());
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


