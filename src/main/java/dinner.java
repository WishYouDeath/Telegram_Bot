public class dinner {
    /*Для реализации уведомлений и отслеживания состояния матчей вам нужно добавить новую команду /setNotification и соответствующую логику. Предлагаю следующий подход:

Добавление команды /setNotification:

В классе MyTelegramBot, в методе addCommands(), добавьте следующую строку:

java
Copy code
commandMap.put(SET_NOTIFICATION, this::handleSetNotificationCommand);
Это предполагает, что у вас уже есть константа SET_NOTIFICATION.

Обработка команды /setNotification:

Добавьте метод для обработки команды /setNotification:

java
Copy code
private void handleSetNotificationCommand(Message message) {
    dialogStateMachine.setDialogState(message.getChatId(), DialogState.SETTING_NOTIFICATION);
    executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
            "Выберите, когда вы хотите получить уведомление:",
            buttonService.setButtons(buttonService.createButtons(asList(BEFORE_START, AFTER_FINISH)))));
}
Создайте новое состояние в DialogStateMachine:

java
Copy code
public static final DialogState SETTING_NOTIFICATION = new DialogState("SETTING_NOTIFICATION");
Обработка выбора времени для уведомления:

Обновите метод handleMessage в соответствии с новым состоянием:

java
Copy code
else if (currentState == DialogState.SETTING_NOTIFICATION) {
    handleSettingNotification(message);
}
Добавьте метод handleSettingNotification:

java
Copy code
private void handleSettingNotification(Message message) {
    if (message.hasText()) {
        String choice = message.getText();
        if (choice.equals(BEFORE_START) || choice.equals(AFTER_FINISH)) {
            // Тут вы можете сохранить выбор пользователя и установить таймер
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
                    "Уведомление будет отправлено " + choice));
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
                    "Пожалуйста, выберите один из предложенных вариантов"));
        }
    }
}
Таймер и уведомление:

Для установки таймера и отправки уведомлений вам, возможно, потребуется использовать ScheduledExecutorService или аналогичный механизм в Java. Ниже представлен пример того, как это может быть сделано:

java
Copy code
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// Добавьте следующее в конструктор класса MyTelegramBot
private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

// Добавьте следующий метод в класс MyTelegramBot
private void scheduleNotification(String teamName, String time, String notificationType, long chatId) {
    // Ваша логика отправки уведомления
    // Пример: scheduler.schedule(() -> sendNotification(teamName, time, notificationType, chatId), delay, TimeUnit.SECONDS);
}

// Пример метода для отправки уведомления
private void sendNotification(String teamName, String time, String notificationType, long chatId) {
    String messageText = String.format("Уведомление для команды %s: %s %s", teamName, notificationType, time);
    executeMessage(sendMessageOperationCreate.createSimpleMessage(new Message(), messageText));
}
Настройте таймер в методе handleSettingNotification, после проверки выбора пользователя.

Обработка окончания матча и начала матча:

В методе, который отвечает за отправку расписания (getTimeTable), добавьте вызов метода scheduleNotification для каждого матча, если у пользователя установлено уведомление.

java
Copy code
// В методе getTimeTable
if (userSelectedNotificationMap.containsKey(message.getChatId())) {
    scheduleNotification(teamName, time, userSelectedNotificationMap.get(message.getChatId()), message.getChatId());
}
Где userSelectedNotificationMap - это еще одна мапа для хранения выбора пользователя относительно уведомлений.

Обратите внимание, что это общий шаблон, и вы, возможно, захотите настроить его под свои конкретные требования. Также, убедитесь, что вы имеете правильную логику для работы с потоками и таймерами, чтобы избежать проблем с многопоточностью.

User
а где ты запустил второй поток? он ведь нам нужен чтобы постоянно проверять состояние матча
ChatGPT
Извините за путаницу. Да, вы правы, для постоянной проверки состояния матча необходим второй поток. Вам может помочь ScheduledExecutorService, который позволяет планировать выполнение задачи с определенной периодичностью.

Изменения в MyTelegramBot:

Добавьте новую переменную в класс MyTelegramBot:

java
Copy code
private final ScheduledExecutorService matchStatusChecker = Executors.newSingleThreadScheduledExecutor();
После регистрации бота в методе main, добавьте следующую строку:

java
Copy code
matchStatusChecker.scheduleAtFixedRate(this::checkMatchStatus, 0, 5, TimeUnit.MINUTES);
Это запустит метод checkMatchStatus каждые 5 минут (вы можете изменить интервал, если нужно).

Метод для проверки статуса матчей:

Добавьте новый метод checkMatchStatus в MyTelegramBot:

java
Copy code
private void checkMatchStatus() {
    // Здесь ваш код для проверки состояния матчей и отправки уведомлений
    // Пример: iterateOverMatchesAndSendNotifications();
}
В этом методе вы можете использовать логику для проверки состояния матчей и отправки уведомлений, основанных на времени.

Итерация по матчам:

Вам также нужно реализовать метод iterateOverMatchesAndSendNotifications. В этом методе вы будете итерироваться по вашим матчам и проверять, нужно ли отправлять уведомления.

java
Copy code
private void iterateOverMatchesAndSendNotifications() {
    // Пример: for (Match match : listOfMatches) {
    //             if (match.isTimeForNotification()) {
    //                 sendNotification(match);
    //             }
    //         }
}
Здесь listOfMatches - это коллекция ваших матчей, и isTimeForNotification - это метод, который проверяет, пришло ли время отправить уведомление для конкретного матча.

Обратите внимание, что вам нужно самостоятельно настроить метод isTimeForNotification и всю логику для проверки состояния матчей. Это зависит от того, как вы получаете информацию о матчах и как они обновляются.


2 / 2

User
я думаю что в методе iterateOverMatchesAndSendNotifications нам нужно итерироваться только по teamName, остальное нам не нужно. Во втором потоке мы будем сравнивать локальное время и время нашего матча. когда он закончится, вызовем getTimeTable, от того самого выбранного пользователем teamName.     import JSON.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.LocalDate;

public class Parser {
    //public static Map<String, String> cache = new HashMap<>();
    private static final Logger logger = LogManager.getLogger(Parser.class);

    public static boolean isTeamMatch(String teamName, Example match) {
        return isTeamNameMatch(teamName, match.getHomeTeam(), match.getAwayTeam(), "ru") ||
                isTeamNameMatch(teamName, match.getHomeTeam(), match.getAwayTeam(), "en");
    }
    public int compareCategory(String category) {
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
    public static boolean isTeamNameMatch(String teamName, HomeTeam homeTeam, AwayTeam awayTeam, String language) {
        if ((homeTeam.getNameTranslations() != null && homeTeam.getNameTranslations().containsKey(language)) ||
                (awayTeam.getNameTranslations() != null && awayTeam.getNameTranslations().containsKey(language))){
            String translatedHomeName = homeTeam.getNameTranslations().get(language)!=null ? homeTeam.getNameTranslations().get(language): "";
            String translatedAwayName = awayTeam.getNameTranslations().get(language)!=null ? awayTeam.getNameTranslations().get(language): "";

            return (translatedAwayName.equalsIgnoreCase(teamName) || translatedHomeName.equalsIgnoreCase(teamName));
        }
        return false;
    }

    public String receiveData(String teamName, String category, String date) {
        int numberOfSport = compareCategory(category);
        try {
            String baseUrl = "https://sportscore1.p.rapidapi.com/sports/" +numberOfSport + "/events/date/";
            //LocalDate currentDate = LocalDate.now() - наша дата UTC+5
            //LocalDate currentDate = LocalDate.now(Clock.systemUTC());// Получение даты сайта (-5 часов)
            //String data = currentDate.toString(); // Преобразование даты в строку с форматом "yyyy-MM-dd"
            String urlString = baseUrl + date;
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
                    Example match = objectMapper.treeToValue(matchNode, Example.class);
                    if (isTeamMatch(teamName, match)) {
                        matchInfoBuilder.append(MatchDataUtil.processMatchData(match,category));
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
ChatGPT
Хорошо, вам нужно добавить второй поток в класс MyTelegramBot, чтобы он мог периодически проверять состояние матчей и отправлять уведомления в чаты, где установлены соответствующие уведомления.

Приведенный ниже код представляет собой дополнение вашего класса для управления вторым потоком. Я добавил новые методы и переменные в MyTelegramBot для работы с вторым потоком:

java
Copy code
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// ... (оставшийся код вашего класса)

public class MyTelegramBot extends TelegramLongPollingBot {
    // ... (оставшиеся переменные)

    // Добавляем переменные для управления вторым потоком
    private final ScheduledExecutorService matchStatusChecker = Executors.newSingleThreadScheduledExecutor();
    private final MatchNotificationManager matchNotificationManager = new MatchNotificationManager();

    public MyTelegramBot() {
        addCommands();
        startMatchStatusChecker();
    }

    private void startMatchStatusChecker() {
        matchStatusChecker.scheduleAtFixedRate(this::checkMatchStatus, 0, 5, TimeUnit.MINUTES);
    }

    private void checkMatchStatus() {
        matchNotificationManager.checkAndSendNotifications(this);
    }

    public void setMatchNotification(Long chatId, String teamName, NotificationType notificationType) {
        matchNotificationManager.setMatchNotification(chatId, teamName, notificationType);
    }

    public void clearMatchNotification(Long chatId, String teamName) {
        matchNotificationManager.clearMatchNotification(chatId, teamName);
    }

    // ... (остальные методы класса)
}
Далее, создадим класс MatchNotificationManager, который будет отслеживать уведомления и отправлять их вовремя:

java
Copy code
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MatchNotificationManager {
    private final Map<Long, Map<String, NotificationType>> matchNotifications = new HashMap<>();

    public MatchNotificationManager() {
        // Возможно, вам также нужно будет настроить дополнительные параметры в конструкторе
        startNotificationScheduler();
    }

    public void setMatchNotification(Long chatId, String teamName, NotificationType notificationType) {
        matchNotifications
            .computeIfAbsent(chatId, k -> new HashMap<>())
            .put(teamName, notificationType);
    }

    public void clearMatchNotification(Long chatId, String teamName) {
        matchNotifications
            .computeIfAbsent(chatId, k -> new HashMap<>())
            .remove(teamName);
    }

    public void checkAndSendNotifications(MyTelegramBot bot) {
        // Здесь ваш код для проверки уведомлений и отправки сообщений
        // Пример: iterateOverChatsAndSendNotifications(bot);
    }

    private void iterateOverChatsAndSendNotifications(MyTelegramBot bot) {
        // Здесь ваш код для итерации по чатам и отправки уведомлений
        // Пример: for (Map.Entry<Long, Map<String, NotificationType>> entry : matchNotifications.entrySet()) {
        //              Long chatId = entry.getKey();
        //              Map<String, NotificationType> teamNotifications = entry.getValue();
        //              for (Map.Entry<String, NotificationType> teamEntry : teamNotifications.entrySet()) {
        //                  String teamName = teamEntry.getKey();
        //                  NotificationType notificationType = teamEntry.getValue();
        //                  if (isTimeForNotification(teamName, notificationType)) {
        //                      bot.sendNotification(chatId, teamName, notificationType);
        //                  }
        //              }
        //         }
    }

    private boolean isTimeForNotification(String teamName, NotificationType notificationType) {
        // Здесь ваш код для проверки времени уведомлений
        // Пример: сравнение текущего времени с временем матча
        return true;
    }

    private void startNotificationScheduler() {
        ScheduledExecutorService notificationScheduler = Executors.newSingleThreadScheduledExecutor();
        notificationScheduler.scheduleAtFixedRate(this::checkAndSendNotifications, 0, 1, TimeUnit.MINUTES);
    }
}
Этот код предполагает, что у вас есть класс NotificationType, представляющий тип уведомления (например, перед матчем или после матча). Вам также нужно добавить метод sendNotification в ваш класс MyTelegramBot, чтобы отправлять уведомления в соответствующий чат.

Примечание: Пожалуйста, адаптируйте код под вашу логику и добавьте дополнительные проверки в методы checkAndSendNotifications и isTimeForNotification в зависимости от ваших требований.

User
напиши класс NotificationType и метод sendNotification в класс MyTelegramBot
ChatGPT
Давайте сначала определим NotificationType. Это может быть простой перечислимый тип (enum) с двумя значениями: BEFORE_MATCH и AFTER_MATCH. Пример такого класса:

java
Copy code
public enum NotificationType {
    BEFORE_MATCH,
    AFTER_MATCH
}
Теперь добавим метод sendNotification в ваш класс MyTelegramBot. Для отправки уведомлений вам понадобится метод, который посылает сообщение пользователю. Опять же, предполагаю, что у вас уже есть метод executeMessage, который вы использовали ранее. Если нет, вам нужно его реализовать.

java
Copy code
// Добавьте метод в ваш класс MyTelegramBot
public void sendNotification(Long chatId, String teamName, NotificationType notificationType) {
    String notificationMessage = buildNotificationMessage(teamName, notificationType);
    SendMessage sendMessage = new SendMessage();
    sendMessage.setChatId(String.valueOf(chatId));
    sendMessage.setText(notificationMessage);

    // Дополнительные настройки сообщения (например, клавиатура, если необходимо)
    // sendMessage.setReplyMarkup(...);

    executeMessage(sendMessage);
}

private String buildNotificationMessage(String teamName, NotificationType notificationType) {
    switch (notificationType) {
        case BEFORE_MATCH:
            return "Уведомление: Матч команды " + teamName + " начнется через 5 минут!";
        case AFTER_MATCH:
            return "Уведомление: Матч команды " + teamName + " завершился!";
        default:
            return "Неизвестный тип уведомления";
    }
}
Примечание: Этот код просто формирует сообщение в зависимости от типа уведомления. Вы можете настроить текст уведомлений по своему усмотрению. Также, обратите внимание, что этот код предполагает, что у вас уже есть метод executeMessage для отправки сообщений. Если это не так, вы должны добавить соответствующую реализацию.

User
checkAndSendNotifications(MyTelegramBot bot) реализуй данный метод для моего бота
ChatGPT
Чтобы реализовать метод checkAndSendNotifications для вашего бота, вам нужно добавить следующий код в ваш класс MyTelegramBot:

java
Copy code
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

// Ваш текущий код класса MyTelegramBot

public class MyTelegramBot extends TelegramLongPollingBot {
    // ... (ваш текущий код)

    // Добавьте метод checkAndSendNotifications
    public void checkAndSendNotifications() {
        // Получите текущее время
        LocalDateTime currentTime = LocalDateTime.now();

        // Пройдитесь по всем пользователям и проверьте их уведомления
        for (Map.Entry<Long, String> entry : userSelectedTeamMap.entrySet()) {
            Long chatId = entry.getKey();
            String teamName = entry.getValue();

            // Получите время начала и конца матча
            LocalDateTime matchStartTime = getMatchStartTime(teamName);
            LocalDateTime matchEndTime = getMatchEndTime(teamName);

            // Отправьте уведомление перед матчем (если осталось менее 5 минут до начала)
            if (currentTime.plusMinutes(5).isAfter(matchStartTime) && currentTime.isBefore(matchStartTime)) {
                sendNotification(chatId, teamName, NotificationType.BEFORE_MATCH);
            }

            // Отправьте уведомление после матча (если матч закончился)
            if (currentTime.isAfter(matchEndTime)) {
                sendNotification(chatId, teamName, NotificationType.AFTER_MATCH);
            }
        }
    }

    private LocalDateTime getMatchStartTime(String teamName) {
        // Здесь добавьте логику для получения времени начала матча для указанной команды
        // Может быть, используя ваш Parser или другие методы
        // Пример:
        // return LocalDateTime.now().plusHours(1); // Время начала матча - текущее время плюс 1 час
        return LocalDateTime.now(); // Замените эту строку на реальный код
    }

    private LocalDateTime getMatchEndTime(String teamName) {
        // Здесь добавьте логику для получения времени окончания матча для указанной команды
        // Пример:
        // return LocalDateTime.now().plusHours(2); // Время окончания матча - текущее время плюс 2 часа
        return LocalDateTime.now().minus(1, ChronoUnit.MINUTES); // Замените эту строку на реальный код
    }

    // Ваш текущий код
}
Этот метод проходится по всем пользователям и проверяет, должны ли им отправить уведомление перед матчем (если осталось менее 5 минут до начала) или после матча (если матч закончился). Пожалуйста, убедитесь, что вы подставили правильные значения времени для ваших матчей в методы getMatchStartTime и getMatchEndTime.

User

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
public class Main {
    public static void main(String [] args){
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyTelegramBot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import static constant.Commands.*;
import constant.DialogState;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MyTelegramBot extends TelegramLongPollingBot {
    SendMessageOperationCreate sendMessageOperationCreate = new SendMessageOperationCreate();
    Map<String, Consumer<Message>> commandMap = new HashMap<>();
    Map<Long, String> userSelectedCategoryMap = new HashMap<>();
    Map<Long, String> userSelectedDateMap = new HashMap<>();
    private final DialogStateMachine dialogStateMachine = new DialogStateMachine();

    public void addCommands() {
        commandMap.put(CATEGORY, this::handleCategoryCommand);
        commandMap.put(START, message -> executeMessage(sendMessageOperationCreate.createGreetingInformation(message)));
        commandMap.put(HELP, message -> executeMessage(sendMessageOperationCreate.createHelpInformation(message)));
        commandMap.put(ABOUT, message -> executeMessage(sendMessageOperationCreate.createBotInformation(message)));
        commandMap.put(AUTHORS, message -> executeMessage(sendMessageOperationCreate.createAuthorsInformation(message)));
        commandMap.put(NOTIFICATION, this::handleGetNotificationCommand);
        commandMap.put(GET, this::handleGetCommand);
    }

    private final ScheduledExecutorService matchStatusChecker = Executors.newSingleThreadScheduledExecutor();
    private final MatchNotificationManager matchNotificationManager = new MatchNotificationManager();
    MyTelegramBot() {
        addCommands();
        startMatchStatusChecker();
    }

    private void startMatchStatusChecker() {
        matchStatusChecker.scheduleAtFixedRate(this::checkMatchStatus, 0, 5, TimeUnit.MINUTES);
    }

    private void checkMatchStatus() {
        matchNotificationManager.checkAndSendNotifications(this);
    }

    public void setMatchNotification(Long chatId, String teamName, NotificationType notificationType) {
        matchNotificationManager.setMatchNotification(chatId, teamName, notificationType);
    }

    public void clearMatchNotification(Long chatId, String teamName) {
        matchNotificationManager.clearMatchNotification(chatId, teamName);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }

    private void handleMessage(Message message) {
        DialogState currentState = dialogStateMachine.getDialogState(message.getChatId());

        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                if (command.equals(GET)) {
                    dialogStateMachine.setDialogState(message.getChatId(), DialogState.WAITING_FOR_TEAM_NAME);
                    executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
                            "Введите название одной команды, относящейся к выбранной категории спорта, и к выбранной дате"));
                }
                else if (command.equals(CATEGORY)) {
                    dialogStateMachine.setDialogState(message.getChatId(), DialogState.CHOOSING_A_CATEGORY);
                    executeMessage(sendMessageOperationCreate.createChooseCategoryMessage(message));
                }
                else if (command.equals(DATE)) {
                    dialogStateMachine.setDialogState(message.getChatId(), DialogState.SETTING_THE_DATE);
                    executeMessage(sendMessageOperationCreate.createChooseDateMessage(message));
                }
                else {
                    commandMap.getOrDefault(command, msg -> executeMessage(sendMessageOperationCreate.wrongCommand(message))).accept(message);
                }
            }
        } else if (currentState == DialogState.WAITING_FOR_TEAM_NAME) {
            handleGetCommand(message);
        }
        else if (currentState == DialogState.SETTING_THE_DATE) {
            handleDateCommand(message);
        }
        else if (currentState == DialogState.CHOOSING_A_CATEGORY) {
            handleCategoryCommand(message);
        }
        else if (currentState == DialogState.SETTING_NOTIFICATION_MATCH) {
            handleSetNotificationMatch(message);
        }
    }

    private void handleGetNotificationCommand(Message message) {
        dialogStateMachine.setDialogState(message.getChatId(), DialogState.SETTING_NOTIFICATION_MATCH);
        executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
                "Введите название команды, для которой хотите установить уведомление"));
    }
    private void handleSetNotificationMatch(Message message) {
        if (message.hasText()) {
            String teamName = message.getText();
            // можно костыльнуть сюда информацию о матче
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
                    "Уведомление установлено для команды: " + teamName));
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message,
                    "Пожалуйста, введите название команды для установки уведомления"));
        }
    }

    private void handleCategoryCommand(Message message) {
        if (message.hasText() && compareInput(message.getText())){
            String category = message.getText();
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Вы установили категорию: " + category));
            userSelectedCategoryMap.put(message.getChatId(),category);
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Неверная категория. Пожалуйста, выберите из предложенных вариантов."));
        }
    }
    private void handleDateCommand(Message message) {
        if (message.hasText() && (message.getText().equalsIgnoreCase("Завтра") ||
                message.getText().equalsIgnoreCase("Сегодня") || message.getText().equalsIgnoreCase("Вчера"))){
            String date = message.getText();
            String finalDate = date;
            if(date.equalsIgnoreCase("Сегодня")) {
                LocalDate currentDate = LocalDate.now(Clock.systemUTC());
                finalDate = currentDate.toString();
            }
            if(date.equalsIgnoreCase("Завтра")){
                LocalDate currentDate = LocalDate.now(Clock.systemUTC()).plusDays(1);
                finalDate = currentDate.toString();
            }
            if(date.equalsIgnoreCase("Вчера")){
                LocalDate currentDate = LocalDate.now(Clock.systemUTC()).minusDays(1);
                finalDate = currentDate.toString();
            }
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Вы выбрали дату: " + finalDate));
            userSelectedDateMap.put(message.getChatId(),finalDate);
            dialogStateMachine.clearDialogState(message.getChatId());
        }
        else if(message.hasText() && message.getText().equalsIgnoreCase("Другая дата")){
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Выберете дату следуя образцу 2023-04-25"));
        }
        else if(message.hasText() && matchesDateOrNot(message.getText())){
            String date = message.getText();
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Вы выбрали дату: " + date));
            userSelectedDateMap.put(message.getChatId(),date);
            dialogStateMachine.clearDialogState(message.getChatId());
        }
        else {
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Неверный формат даты. Пожалуйста, выберите из предложенных вариантов или отформатируйте вашу дату согласно образцу"));
        }
    }
    private void handleGetCommand(Message message) {
        if (message.hasText()) {
            String teamName = message.getText();
            executeMessage(sendMessageOperationCreate.getTimeTable(message, teamName, userSelectedCategoryMap,userSelectedDateMap));
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Пожалуйста, введите название команды (teamName):"));
        }
    }
    private boolean matchesDateOrNot(String date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try{
            LocalDate.parse(date, formatter);
            return true;
        } catch (ExceptionHandler.DateProcessingException e) {
            return false;
        }
    }
    private boolean compareInput(String category){
        return(category.equalsIgnoreCase("Хоккей")||category.equalsIgnoreCase("Футбол")||category.equalsIgnoreCase("Волейбол")||
                category.equalsIgnoreCase("Баскетбол")||category.equalsIgnoreCase("Теннис")||category.equalsIgnoreCase("Гандбол"));
    }

    String nameBot = System.getenv("Telegram_Name");
    String apiBot = System.getenv("Telegram_API");

    @Override
    public String getBotUsername() {
        return nameBot;
    }

    @Override
    public String getBotToken() {
        return apiBot;
    }

    private <T extends BotApiMethod> void executeMessage(T sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            System.out.println("Не удалось отправить сообщение" + e.getCause());
        }
    }
}



подключи здесь 2 поток который будет постоянно проверять состояние уведомлений
ChatGPT
Чтобы добавить второй поток для постоянной проверки состояния уведомлений, вы можете внести следующие изменения в ваш класс MyTelegramBot. Вам понадобится использовать класс ScheduledExecutorService для создания второго потока. Вот как вы можете это сделать:

Объявите и инициализируйте второй ScheduledExecutorService в вашем классе MyTelegramBot:
java
Copy code
private final ScheduledExecutorService notificationChecker = Executors.newSingleThreadScheduledExecutor();
В методе MyTelegramBot добавьте вызов метода startNotificationChecker для запуска второго потока:
java
Copy code
MyTelegramBot() {
    addCommands();
    startMatchStatusChecker();
    startNotificationChecker(); // Добавьте эту строку
}
Создайте метод startNotificationChecker для настройки и запуска второго потока:
java
Copy code
private void startNotificationChecker() {
    notificationChecker.scheduleAtFixedRate(this::checkNotifications, 0, 1, TimeUnit.MINUTES);
}
Создайте метод checkNotifications для выполнения проверки состояния уведомлений:
java
Copy code
private void checkNotifications() {
    // В этом методе вызовите проверку уведомлений, например:
    matchNotificationManager.checkAndSendNotifications(this);
}
Теперь у вас должен быть второй поток, который будет каждую минуту проверять состояние уведомлений. Пожалуйста, убедитесь, что это время проверки подходит для вашего приложения, и, если необходимо, настройте интервал в методе notificationChecker.scheduleAtFixedRate.
    */
}
