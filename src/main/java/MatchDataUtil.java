import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;
public class MatchDataUtil {
    public static String processMatchData(JsonNode matchNode) {
        Map<String, String> periodMap = initializePeriodMap();

        StringBuilder matchInfoBuilder = new StringBuilder();
        String league = getTranslation(matchNode, "league");
        String homeTeam = getTranslation(matchNode, "home_team");
        String awayTeam = getTranslation(matchNode, "away_team");
        String status = matchNode.hasNonNull("status") ? matchNode.get("status").asText() : "Н/Д";
        int currentHomeScore = matchNode.hasNonNull("home_score") ? matchNode.get("home_score").get("current").asInt() : 0;
        int currentAwayScore = matchNode.hasNonNull("away_score") ? matchNode.get("away_score").get("current").asInt() : 0;
        String formattedStartTime = DateTimeUtil.getDate(matchNode);
        String currentPeriod = matchNode.hasNonNull("status_more") ? matchNode.get("status_more").asText() : "Н/Д";
        currentPeriod = periodMap.getOrDefault(currentPeriod.toLowerCase(), "Н/Д");

        switch (status.toLowerCase()) {
            case "finished":
                matchInfoBuilder.append(String.format("Матч в лиге: '%s' завершился\n%s\t%d:%d\t%s\nМатч был %s",
                        league, homeTeam, currentHomeScore, currentAwayScore, awayTeam, formattedStartTime));
                break;
            case "notstarted":
                matchInfoBuilder.append(String.format("Матч в лиге: '%s' ещё не начался\nМатч '%s - %s' будет в это время: %s\n",
                        league, homeTeam, awayTeam, formattedStartTime));
                break;
            case "inprogress":
                matchInfoBuilder.append(String.format("Матч %s - %s в лиге: '%s' уже начался\nСейчас в матче %s\nТекущий счёт %d:%d",
                        homeTeam, awayTeam, league, currentPeriod, currentHomeScore, currentAwayScore));
                break;
            case "postponed":
                matchInfoBuilder.append(String.format("Матч между %s и %s в лиге: '%s' был отложен\n", homeTeam, awayTeam, league));
                break;
            default:
                matchInfoBuilder.append("Неизвестный статус матча");
        }

        return matchInfoBuilder.toString();
    }

    private static Map<String, String> initializePeriodMap() {
        Map<String, String> periodMap = new HashMap<>();
        periodMap.put("halftime", "перерыв");
        periodMap.put("1st half", "первый период");
        periodMap.put("2nd half", "второй период");
        periodMap.put("90+", "овертайм");
        return periodMap;
    }

    private static String getTranslation(JsonNode node, String field) {
        if (node.hasNonNull(field) && node.get(field).hasNonNull("name_translations") && node.get(field).get("name_translations").hasNonNull("ru")) {
            return node.get(field).get("name_translations").get("ru").asText();
        }
        if (node.hasNonNull(field) && node.get(field).hasNonNull("name_translations") && node.get(field).get("name_translations").hasNonNull("en")) {
            return node.get(field).get("name_translations").get("en").asText();
        }
        return "Н/Д";
    }
}