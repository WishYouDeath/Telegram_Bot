import JSON.*;
import java.util.HashMap;
import java.util.Map;
public class MatchDataUtil {
    public static String processMatchData(Example example, String category) {
        Map<String, String> periodMap = initializePeriodMap();
        StringBuilder matchInfoBuilder = new StringBuilder();

        // Получение информации о матче
        HomeTeam homeTeam = example.getHomeTeam();
        AwayTeam awayTeam = example.getAwayTeam();
        String nameHomeTeam = getTranslatedHomeName(homeTeam);
        String nameAwayTeam = getTranslatedAwayName(awayTeam);

        League league = example.getLeague();
        String nameLeague = getTranslatedName(league);

        int currentHomeScore = example.getHomeScore() != null && example.getHomeScore().getCurrent() != null ? example.getHomeScore().getCurrent() : 0;
        int currentAwayScore = example.getAwayScore() != null && example.getAwayScore().getCurrent() != null ? example.getAwayScore().getCurrent() : 0;

        String status = example.getStatus() != null ? example.getStatus() : "Н/Д";
        String currentPeriod = example.getStatusMore() != null ? example.getStatusMore() : "Н/Д";
        currentPeriod = periodMap.getOrDefault(currentPeriod.toLowerCase(), "Н/Д");

        String formattedStartTime = DateTimeUtil.getDate(example);

        switch (status.toLowerCase()) {
            case "finished":
                matchInfoBuilder.append(String.format("Выбранная категория:"+ category + "\nМатч в лиге: '%s' завершился\n%s\t%d:%d\t%s\nМатч был %s",
                        nameLeague, nameHomeTeam, currentHomeScore, currentAwayScore, nameAwayTeam, formattedStartTime));
                break;
            case "notstarted":
                matchInfoBuilder.append(String.format("Выбранная категория:"+ category + "\nМатч в лиге: '%s' ещё не начался\nМатч '%s : %s' будет в это время: %s\n",
                        nameLeague, nameHomeTeam, nameAwayTeam, formattedStartTime));
                break;
            case "inprogress":
                matchInfoBuilder.append(String.format("Выбранная категория:"+ category + "\nМатч %s : %s в лиге: '%s' уже начался\nСейчас в матче %s\nТекущий счёт %d:%d",
                        nameHomeTeam, nameAwayTeam, nameLeague, currentPeriod, currentHomeScore, currentAwayScore));
                break;
            case "postponed":
                matchInfoBuilder.append(String.format("Выбранная категория:"+ category + "\nМатч между %s и %s в лиге: '%s' был отложен\n", nameHomeTeam, nameAwayTeam, nameLeague));
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

    private static String getTranslatedHomeName(HomeTeam team) {
        if (team.getNameTranslations() != null) {
            if (team.getNameTranslations().containsKey("ru")) {
                return team.getNameTranslations().get("ru");
            } else if (team.getNameTranslations().containsKey("en")) {
                return team.getNameTranslations().get("en");
            }
        }
        return team.getName();
    }private static String getTranslatedAwayName(AwayTeam team) {
        if (team.getNameTranslations() != null) {
            if (team.getNameTranslations().containsKey("ru")) {
                return team.getNameTranslations().get("ru");
            } else if (team.getNameTranslations().containsKey("en")) {
                return team.getNameTranslations().get("en");
            }
        }
        return team.getName();
    }


    private static String getTranslatedName(League league) {
        if (league.getNameTranslations() != null) {
            if (league.getNameTranslations().containsKey("ru")) {
                return league.getNameTranslations().get("ru");
            } else if (league.getNameTranslations().containsKey("en")) {
                return league.getNameTranslations().get("en");
            }
        }
        return league.getName();
    }
}
