import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Parser {
    String SITE_API = System.getenv("SITE_API");
    public String getDate(JSONObject matchObject) {
        String startTime = matchObject.getString("start_at");
        try {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startDateTime = LocalDateTime.parse(startTime, inputFormatter);
            LocalDateTime adjustedDateTime = startDateTime.plusHours(5);
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm");
            return adjustedDateTime.format(outputFormatter);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // или другой вариант обработки ошибки
        }
    }
    public int matchNumber (JSONArray matchesArray, String teamName, String language) {
        for (int i = 0; i < matchesArray.length(); i++) {
            JSONObject matchObject = matchesArray.getJSONObject(i); //Перебираем в цикле каждый матч
            JSONObject homeTeamObject = matchObject.getJSONObject("home_team");
            JSONObject homeTeamNameTranslations = homeTeamObject.getJSONObject("name_translations");
            String homeTeamName = homeTeamNameTranslations != null ? homeTeamNameTranslations.optString(language) : homeTeamObject.optString("home_team");

            JSONObject awayTeamObject = matchObject.getJSONObject("away_team");
            JSONObject awayTeamNameTranslations = awayTeamObject.getJSONObject("name_translations");
            String awayTeamName = awayTeamNameTranslations != null ? awayTeamNameTranslations.optString(language) : awayTeamObject.optString("away_team");
            if(homeTeamName.equalsIgnoreCase(teamName) || awayTeamName.equalsIgnoreCase(teamName)) {
                return i;
            }
        }
        return -1;
    }

    public String receiveData(String teamName) {
        try {
            String data = "2023-10-21";
            String language = "ru";
            // Установка параметров запроса
            String urlString = "https://sportscore1.p.rapidapi.com/sports/1/events/date/" + data;
            /*
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("X-RapidAPI-Key", SITE_API)
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            int responseCode = response.statusCode();
            String responseBody = response.body();

            if (responseCode == 200) {
                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray matchesArray = jsonObject.getJSONArray("data");*/
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

                // Поиск и формирование информации о конкретном матче
                StringBuilder result = new StringBuilder(); // Создаем StringBuilder для сбора информации о матчах

                int numberOfTheMatch = matchNumber (matchesArray,teamName,language);
                if (numberOfTheMatch == -1) {
                    result.append("Такого матча сегодня нет");
                    return result.toString();
                }
                else{
                    JSONObject matchObject = matchesArray.getJSONObject(numberOfTheMatch); //Перебираем в цикле каждый матч

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
                    } else if ((status.equalsIgnoreCase("notstarted"))){
                        result.append("Матч в лиге: '").append(leagueName).append("' ещё не начался\n");
                        result.append("Матч '").append(homeTeamName).append(" - ").append(awayTeamName).append("' будет в это время: ").append(newStartTime).append("\n");
                    } else if ((status.equalsIgnoreCase("inprogress"))){
                        String currentPeriod = matchObject.getString("status_more");
                        result.append("Матч ").append(homeTeamName).append(" - ").append(awayTeamName).append(" в лиге: '").append(leagueName).append("' уже начался");
                        result.append("Идёт ").append(currentPeriod).append(" период");
                        result.append("Текущий счёт ").append(scoreHome).append(":").append(scoreAway);
                    } else if ((status.equalsIgnoreCase("postponed"))){
                        result.append("Матч между ").append(homeTeamName).append(" и ").append(awayTeamName).append(" в лиге: '").append(leagueName).append("' был отложен\n");
                    }
                    return result.toString(); // Возвращаем информацию как строку
                }
            } else {
                return "Ошибка в запросе: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Произошла ошибка при получении информации о матчах.";
        }
    }
}


        




