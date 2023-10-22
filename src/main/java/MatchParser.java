import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class MatchParser {
    public static String getDate(JSONObject matchObject) {
        String startTime = matchObject.getString("start_at");
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startTime, inputFormatter);
            LocalDateTime adjustedDateTime = startDateTime.plusHours(5);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
            return adjustedDateTime.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getMatchIndex(JSONArray matchesArray, String teamName, String language) {
        for (int i = 0; i < matchesArray.length(); i++) {
            JSONObject matchObject = matchesArray.getJSONObject(i);
            JSONObject homeTeamObject = matchObject.getJSONObject("home_team");
            JSONObject homeTeamNameTranslations = homeTeamObject.getJSONObject("name_translations");
            String homeTeamName = homeTeamNameTranslations != null ? homeTeamNameTranslations.optString(language) : homeTeamObject.optString("home_team");

            JSONObject awayTeamObject = matchObject.getJSONObject("away_team");
            JSONObject awayTeamNameTranslations = awayTeamObject.getJSONObject("name_translations");
            String awayTeamName = awayTeamNameTranslations != null ? awayTeamNameTranslations.optString(language) : awayTeamObject.optString("away_team");

            if (homeTeamName.equalsIgnoreCase(teamName) || awayTeamName.equalsIgnoreCase(teamName)) {
                return i;
            }
        }
        return -1;
    }
    public static String parseMatchData(JSONObject matchObject, String language) {
        StringBuilder result = new StringBuilder();
        JSONObject leagueObject = matchObject.getJSONObject("league");
        JSONObject leagueTranslations = leagueObject.getJSONObject("name_translations");
        String leagueName = leagueTranslations != null ? leagueTranslations.optString(language) : leagueTranslations.optString("name_translations");

        JSONObject homeTeamObject = matchObject.getJSONObject("home_team");
        JSONObject homeTeamNameTranslations = homeTeamObject.getJSONObject("name_translations");
        String homeTeamName = homeTeamNameTranslations != null ? homeTeamNameTranslations.optString(language) : homeTeamObject.optString("home_team");

        JSONObject awayTeamObject = matchObject.getJSONObject("away_team");
        JSONObject awayTeamNameTranslations = awayTeamObject.getJSONObject("name_translations");
        String awayTeamName = awayTeamNameTranslations != null ? awayTeamNameTranslations.optString(language) : awayTeamObject.optString("away_team");

        String status = matchObject.getString("status");// Статус игры: inprogress  finished postponed(отложен) notstarted
        JSONObject scoreHomeTeam = matchObject.optJSONObject("home_score");
        int scoreHome = scoreHomeTeam != null ? scoreHomeTeam.optInt("current", 0) : 0;

        JSONObject scoreAwayTeam = matchObject.optJSONObject("away_score");
        int scoreAway = scoreAwayTeam != null ? scoreAwayTeam.optInt("current", 0) : 0;

        String newStartTime = getDate(matchObject);

        if (status.equalsIgnoreCase("finished")) {
            result.append("Матч в лиге: '").append(leagueName).append("' завершился\n");
            result.append(homeTeamName).append('\t').append(scoreHome).append(":").append(scoreAway).append('\t').append(awayTeamName).append("\n");
            result.append("Матч был в это время: ").append(newStartTime).append("\n");
        } else if ((status.equalsIgnoreCase("notstarted"))) {
            result.append("Матч в лиге: '").append(leagueName).append("' ещё не начался\n");
            result.append("Матч '").append(homeTeamName).append(" - ").append(awayTeamName).append("' будет в это время: ").append(newStartTime).append("\n");
        } else if ((status.equalsIgnoreCase("inprogress"))) {
            String currentPeriod = matchObject.getString("status_more");
            result.append("Матч ").append(homeTeamName).append(" - ").append(awayTeamName).append(" в лиге: '").append(leagueName).append("' уже начался");
            result.append("Идёт ").append(currentPeriod).append(" период");
            result.append("Текущий счёт ").append(scoreHome).append(":").append(scoreAway);
        } else if ((status.equalsIgnoreCase("postponed"))) {
            result.append("Матч между ").append(homeTeamName).append(" и ").append(awayTeamName).append(" в лиге: '").append(leagueName).append("' был отложен\n");
        }
        return result.toString(); // Возвращаем информацию как строку
    }
}
