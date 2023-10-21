import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Parser {
    String SITE_API = System.getenv("SITE_API");

    public boolean isMatchFound(String homeTeamName, String awayTeamName, String teamName) {
        return (homeTeamName.equalsIgnoreCase(teamName) || awayTeamName.equalsIgnoreCase(teamName));
    }

    public String receiveData(String teamName) {
        try {
            String data = "2023-10-21";
            String language = "ru";
            // Установка параметров запроса
            String urlString = "https://sportscore1.p.rapidapi.com/sports/1/events/date/" + data;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", SITE_API);

            // Получение и обработка ответа
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Обработка JSON-ответа
                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray matchesArray = jsonObject.getJSONArray("data");
                boolean matchFound = false;

                // Поиск и формирование информации о конкретном матче
                StringBuilder result = new StringBuilder(); // Создаем StringBuilder для сбора информации о матчах

                for (int i = 0; i < matchesArray.length(); i++) {
                    JSONObject matchObject = matchesArray.getJSONObject(i); //Перебираем в цикле каждый матч
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

                    //Устанавливаем имя матча
                    // String teamName = "Россия";

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String startTime = matchObject.getString("start_at");
                    Calendar cal = Calendar.getInstance();
                    Date date = format.parse(startTime);
                    cal.setTime(date);
                    cal.add(Calendar.HOUR_OF_DAY, 5);
                    date = cal.getTime();
                    String newStartTime = format.format(date);

                    if (isMatchFound(homeTeamName, awayTeamName, teamName) && (status.equalsIgnoreCase("finished"))) {
                        matchFound = true;
                        result.append("Матч в лиге: '").append(leagueName).append("' завершился\n");
                        result.append(homeTeamName).append('\t').append(scoreHome).append(":").append(scoreAway).append('\t').append(awayTeamName).append("\n");
                        result.append("Матч был в это время: ").append(newStartTime).append("\n");
                        break;
                    } else if (isMatchFound(homeTeamName, awayTeamName, teamName) && (status.equalsIgnoreCase("notstarted"))){
                        matchFound = true;
                        result.append("Матч в лиге: '").append(leagueName).append("' ещё не начался\n");
                        result.append("Матч '").append(homeTeamName).append(" - ").append(awayTeamName).append("' будет в это время: ").append(newStartTime).append("\n");
                        break;
                    } else if (isMatchFound(homeTeamName,awayTeamName,teamName) && (status.equalsIgnoreCase("inprogress"))){
                        matchFound = true;
                        String currentPeriod = matchObject.getString("status_more");
                        result.append("Матч ").append(homeTeamName).append(" - ").append(awayTeamName).append(" в лиге: '").append(leagueName).append("уже начался");
                        result.append("Идёт ").append(currentPeriod).append(" период");
                        result.append("Текущий счёт ").append(scoreHome).append(":").append(scoreAway);
                        break;
                    } else if (isMatchFound(homeTeamName, awayTeamName, teamName) && (status.equalsIgnoreCase("postponed"))){
                        matchFound = true;
                        result.append("Матч между ").append(homeTeamName).append(" и ").append(awayTeamName).append(" в лиге: '").append(leagueName).append("' был отложен\n");
                        break;
                    }
                }
                if (!matchFound) {
                    result.append("Такого матча сегодня нет");
                    return result.toString();
                }
                System.out.println(result);
                //System.out.println(teamName);
                return result.toString(); // Возвращаем информацию как строку
            } else {
                return "Ошибка в запросе: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Произошла ошибка при получении информации о матчах.";
        }
    }
}


        




