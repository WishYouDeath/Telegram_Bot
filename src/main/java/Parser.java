import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.HashMap;
import java.util.Map;
public class Parser {
    private static Map<String, String> cache = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Parser.class);
    public String receiveData(String teamName) {
        if (cache.containsKey(teamName)) {
            return cache.get(teamName);
        }
        try {
            String language = "ru";
            String baseUrl = "https://sportscore1.p.rapidapi.com/sports/1/events/date/";
            String data = "2023-10-21";
            String urlString = baseUrl + data;
            String responseBody = APIRequest.sendGETRequest(urlString);

            if (responseBody == null || responseBody.isEmpty()) {
                return "Произошла ошибка при получении информации о матчах.";
            }

            JSONObject jsonObject = new JSONObject(responseBody);
            JSONArray matchesArray = jsonObject.getJSONArray("data");

            StringBuilder result = new StringBuilder();
            int numberOfTheMatch = MatchParser.getMatchIndex(matchesArray, teamName, language);
            if (numberOfTheMatch == -1) {
                result.append("Такого матча сегодня нет");
                cache.put(teamName, result.toString());
                return result.toString();
            } else {
                JSONObject matchObject = matchesArray.getJSONObject(numberOfTheMatch);
                String matchData = MatchParser.parseMatchData(matchObject, language);
                result.append(matchData);
                cache.put(teamName, result.toString());
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("An error occurred: {}", e.getMessage());
            return "Произошла ошибка при получении информации о матчах.";
        }
    }
}




