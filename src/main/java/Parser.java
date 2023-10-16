import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*
public class ApiRequest {
    private static final String API_KEY = "";
    public static String sendGetRequest(String urlString){
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-RapidAPI-Key", API_KEY);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                return response.toString();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public class JsonParser {
        public JSONArray getJsonArray(String jsonResponse, String key) {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            return jsonObject.getJSONArray(key);
        }
    }
}*/

public class Parser {

    public boolean isMatchFound(String homeTeamName, String awayTeamName, String teamName) {
        return (homeTeamName.equalsIgnoreCase(teamName) || awayTeamName.equalsIgnoreCase(teamName));
    }

    public void receiveData() {
            try {
                String data = "2023-10-16";
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

                    // Поиск и вывод информации о конкретном матче
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
                        String teamName = "Россия";

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String startTime = matchObject.getString("start_at");
                        Calendar cal = Calendar.getInstance();
                        Date date = format.parse(startTime);
                        cal.setTime(date);
                        cal.add(Calendar.HOUR_OF_DAY, 5);
                        date = cal.getTime();
                        String newStartTime = format.format(date);

                        if (isMatchFound(homeTeamName,awayTeamName,teamName) && (status.equalsIgnoreCase("finished"))) {
                            matchFound = true;

                            System.out.println("Матч в лиге: '" + leagueName  + "' завершился");
                            System.out.println(homeTeamName + '\t' + scoreHome + ":" + scoreAway + '\t' + awayTeamName);
                            System.out.println("Матч был в это время: " + newStartTime);
                            break;
                        } else if (isMatchFound(homeTeamName,awayTeamName,teamName) && (status.equalsIgnoreCase("notstarted"))){
                            matchFound = true;
                            System.out.println("Матч в лиге: '" + leagueName  + "' ещё не начался");
                            System.out.println("Матч '" + homeTeamName + " - " + awayTeamName + "' будет в это время: " + newStartTime);
                            break;
                        } else if (isMatchFound(homeTeamName,awayTeamName,teamName) && (status.equalsIgnoreCase("inprogress"))){
                            matchFound = true;
                            String currentPeriod = matchObject.getString("status_more");
                            System.out.println("Матч " + homeTeamName + " - " + awayTeamName + " в лиге: '" + leagueName  +"' уже начался");
                            System.out.println("Идёт " + currentPeriod + " период");
                            System.out.println("Текущий счёт " + scoreHome + ":" + scoreAway);
                            break;
                        } else if (isMatchFound(homeTeamName,awayTeamName,teamName) && (status.equalsIgnoreCase("postponed"))){
                            matchFound = true;
                            System.out.println("Матч между " + homeTeamName + " и " + awayTeamName + "в лиге: '" + leagueName  +"' был отложен");
                            break;
                        }
                    }
                    if (!matchFound) {
                        System.out.println("Такого матча сегодня нет");
                    }
                } else {
                    System.out.println("Ошибка в запросе: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
}
        




