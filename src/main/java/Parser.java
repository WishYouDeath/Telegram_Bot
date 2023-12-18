import JSON.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Clock;
import java.time.LocalDate;

public class Parser {
    private Example match;
    public Example getMatch() {
        return match;
    }

    public void setMatch(Example match) {
        this.match = match;
    }
    private static final Logger logger = LogManager.getLogger(Parser.class);

    private static boolean isTeamMatch(String teamName, Example match) {
        return isTeamNameMatch(teamName, match.getHomeTeam(), match.getAwayTeam(), "ru") ||
                isTeamNameMatch(teamName, match.getHomeTeam(), match.getAwayTeam(), "en");
    }

    private int compareCategory(String category) {
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

    private static boolean isTeamNameMatch(String teamName, HomeTeam homeTeam, AwayTeam awayTeam, String language) {
        if ((homeTeam.getNameTranslations() != null && homeTeam.getNameTranslations().containsKey(language)) ||
                (awayTeam.getNameTranslations() != null && awayTeam.getNameTranslations().containsKey(language))) {
            String translatedHomeName = homeTeam.getNameTranslations().get(language) != null ? homeTeam.getNameTranslations().get(language) : "";
            String translatedAwayName = awayTeam.getNameTranslations().get(language) != null ? awayTeam.getNameTranslations().get(language) : "";

            return (translatedAwayName.equalsIgnoreCase(teamName) || translatedHomeName.equalsIgnoreCase(teamName));
        }
        return false;
    }

    public String receiveData(String teamName, String category, String date) {
        LocalDate currentDate = LocalDate.now(Clock.systemUTC());
        if (category == null || date == null) {
            category = category == null ? "Футбол" : category;
            date = date == null ? currentDate.toString() : date;
        }
        int numberOfSport = compareCategory(category);
        try {
            String baseUrl = "https://sportscore1.p.rapidapi.com/sports/" + numberOfSport + "/events/date/";
            String urlString = baseUrl + date;
            String responseBody = APIRequest.sendGETRequest(urlString);
            if (responseBody.isEmpty()) {
                logger.error("Произошла ошибка при обработке данных с сайта: получен пустой ответ");
                throw new ExceptionHandler.EmptyResponseException("Произошла ошибка при обработке данных с сайта");
            }
            return GetInfo(responseBody,teamName,category);
        } catch (Exception e) {
            logger.error("Произошла ошибка при отправке HTTP-запроса", e);
            throw new ExceptionHandler.HttpRequestException("Произошла ошибка при отправке HTTP-запроса", e);
        }
    }
    private String GetInfo(String responseBody, String teamName, String category){
        try {
            StringBuilder matchInfoBuilder = new StringBuilder();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode matchesNode = rootNode.get("data");
            for (JsonNode matchNode : matchesNode) {
                match = objectMapper.treeToValue(matchNode, Example.class);
                if (isTeamMatch(teamName, match)) {
                    matchInfoBuilder.append(MatchDataUtil.processMatchData(match, category));
                    setMatch(match);
                    return matchInfoBuilder.toString();
                }
            }
            matchInfoBuilder.append("Такого матча сегодня нет");
            return matchInfoBuilder.toString();

        } catch (JsonProcessingException e) {
            logger.error("Произошла ошибка при обработке данных с сайта", e);
            throw new ExceptionHandler.DataProcessingException("Произошла ошибка при обработке данных с сайта", e);
        }
    }
}


