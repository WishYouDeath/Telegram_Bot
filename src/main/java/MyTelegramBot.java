import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import static constant.Commands.*;
import constant.DialogState;


public class MyTelegramBot extends TelegramLongPollingBot {
    SendMessageOperationCreate sendMessageOperationCreate = new SendMessageOperationCreate();
    Map<String, Consumer<Message>> commandMap = new HashMap<>();
    static Map<Long, String> userSelectedCategoryMap = new HashMap<>();
    Map<Long, String> userSelectedDateMap = new HashMap<>();
    Map<Long, String> userSelectedTimeNotification = new HashMap<>();
    Map<Long, String> userSelectedMatchNotification = new HashMap<>();
    private final DialogStateMachine dialogStateMachine = new DialogStateMachine();

    Map<Long, String> matchNotificationMap = new HashMap<>();

    public void addCommands() {
        commandMap.put(CATEGORY, this::handleCategoryCommand);
        commandMap.put(START, message -> executeMessage(sendMessageOperationCreate.createGreetingInformation(message)));
        commandMap.put(HELP, message -> executeMessage(sendMessageOperationCreate.createHelpInformation(message)));
        commandMap.put(ABOUT, message -> executeMessage(sendMessageOperationCreate.createBotInformation(message)));
        commandMap.put(AUTHORS, message -> executeMessage(sendMessageOperationCreate.createAuthorsInformation(message)));
        commandMap.put(GET, this::handleGetCommand);
    }


    MyTelegramBot() {
        addCommands();
        //startNotificationSender();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            handleMessage(update.getMessage());
        }
    }
//    private void startNotificationSender() {
//        TelegramNotificationSender notificationSender = new TelegramNotificationSender(this, notificationMap);
//        Thread senderThread = new Thread(notificationSender);
//        senderThread.start();
//    }

    private void handleMessage(Message message) {
        DialogState currentState = dialogStateMachine.getDialogState(message.getChatId());

        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());

                if (command.equals(GET)) {
                    dialogStateMachine.setDialogState(message.getChatId(), DialogState.WAITING_FOR_TEAM_NAME);
                    executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                            "Введите название одной команды, относящейся к выбранной категории спорта, и к выбранной дате"));
                } else if (command.equals(CATEGORY)) {
                    dialogStateMachine.setDialogState(message.getChatId(), DialogState.CHOOSING_A_CATEGORY);
                    executeMessage(sendMessageOperationCreate.createChooseCategoryMessage(message));
                } else if (command.equals(DATE)) {
                    dialogStateMachine.setDialogState(message.getChatId(), DialogState.SETTING_THE_DATE);
                    executeMessage(sendMessageOperationCreate.createChooseDateMessage(message));
                }else if (command.equals(NOTIFICATION)) {
                        dialogStateMachine.setDialogState(message.getChatId(), DialogState.SETTING_NOTIFICATION_TIME);
                        executeMessage(sendMessageOperationCreate.setNotificationTime(message));
                } else {
                    commandMap.getOrDefault(command, msg -> executeMessage(sendMessageOperationCreate.wrongCommand(message))).accept(message);
                }
            }
        } else if (currentState == DialogState.WAITING_FOR_TEAM_NAME) {
            handleGetCommand(message);
        } else if (currentState == DialogState.SETTING_NOTIFICATION_MATCH) {
                handleSetNotificationMatch(message);
        } else if (currentState == DialogState.SETTING_THE_DATE) {
            handleDateCommand(message);
        } else if (currentState == DialogState.CHOOSING_A_CATEGORY) {
            handleCategoryCommand(message);
        } else if (currentState == DialogState.SETTING_NOTIFICATION_TIME) {
            handleSetNotificationTime(message);
        }
    }
    private void handleGetNotificationCommand(Message message) {
        dialogStateMachine.setDialogState(message.getChatId(), DialogState.SETTING_NOTIFICATION_MATCH);
        executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                "Введите название команды, для которой хотите установить уведомление"));
    }
    private void handleSetNotificationTime(Message message) {
        if (message.hasText()) {
            String time = message.getText();
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                    "Вы выбрали напоминание: " + time));
            userSelectedTimeNotification.put(message.getChatId(), time);
            handleGetNotificationCommand(message);
        } else {
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                    "Пожалуйста, выберете когда хотите получить напоминание о матче"));
        }
    }
    private void handleSetNotificationMatch(Message message) {
        if (message.hasText()) {
            String team = message.getText();
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                    "Напоминание на команду " + team + " установлено"));
            userSelectedMatchNotification.put(message.getChatId(), team);
            dialogStateMachine.clearDialogState(message.getChatId());
            //Вызывать напоминание из него распарсить время и всё. Если матча нет то высказать это
            String MatchInfo = GetNotificationTime.getTimeForNotification(message, team, userSelectedCategoryMap, userSelectedDateMap);
            if (MatchInfo.equals("Такого матча сегодня нет")){
                executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                        "Такого матча сегодня нет, выберете другой матч для установки уведомления"));
            }
            else{
                String time = GetNotificationTime.ParseDate(Parser.match, userSelectedCategoryMap, message);
                switch (time){
                    case("Матч уже начался!"):
                        executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                                "Матч уже начался!"));
                        return;
                    case("Матч был отложен"):
                        executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                                "Матч был отложен"));
                        return;
                    case("Неизвестный статус матча"):
                        executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                                "Неизвестный статус матча"));
                        return;
                    case("Время матча не наступило"):
                       createAndStartSecondThread(message);
                        return;
                        //Если мы не в цикле значит матч уже должен начаться и нужно отправить оповещение
                }
                executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                        time));
            }
        } else {
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                    "Пожалуйста, выберете команду для установки напоминания"));
        }
    }
    public static long calculateCheckingInterval(Message message) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime matchTime = GetNotificationTime.ParseDate(Parser.match, userSelectedCategoryMap, message);

        long durationInSeconds = Duration.between(now, matchTime).toSeconds();

        if (durationInSeconds < 0) {
            // Время матча уже наступило
            return 0;
        }

        long checkingInterval = durationInSeconds / 10; // Время между проверками (каждые 10 секунд)

        return checkingInterval;
    }
    public void createAndStartSecondThread(Message message) {
        Thread secondThread = new Thread(() -> {
            long checkingInterval = calculateCheckingInterval(message);

            while (true) {
                try {
                    Thread.sleep(checkingInterval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                String currentMatchStatus = GetNotificationTime.ParseDate(Parser.match, userSelectedCategoryMap, message);

                if (!currentMatchStatus.equals("Время матча не наступило")) {
                    executeMessage(SendMessageOperationCreate.createSimpleMessage(message,
                            "Матч начинается через 5 минут: " + currentMatchStatus));
                    break;
                }
            }
        });

        secondThread.start();
    }

    private void handleCategoryCommand(Message message) {
        if (message.hasText() && compareInput(message.getText())) {
            String category = message.getText();
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Вы установили категорию: " + category));
            userSelectedCategoryMap.put(message.getChatId(), category);
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Неверная категория. Пожалуйста, выберите из предложенных вариантов."));
        }
    }

    private void handleDateCommand(Message message) {
        if (message.hasText() && (message.getText().equalsIgnoreCase("Завтра") ||
                message.getText().equalsIgnoreCase("Сегодня") || message.getText().equalsIgnoreCase("Вчера"))) {
            String date = message.getText();
            String finalDate = date;
            if (date.equalsIgnoreCase("Сегодня")) {
                LocalDate currentDate = LocalDate.now(Clock.systemUTC());
                finalDate = currentDate.toString();
            }
            if (date.equalsIgnoreCase("Завтра")) {
                LocalDate currentDate = LocalDate.now(Clock.systemUTC()).plusDays(1);
                finalDate = currentDate.toString();
            }
            if (date.equalsIgnoreCase("Вчера")) {
                LocalDate currentDate = LocalDate.now(Clock.systemUTC()).minusDays(1);
                finalDate = currentDate.toString();
            }
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Вы выбрали дату: " + finalDate));
            userSelectedDateMap.put(message.getChatId(), finalDate);
            dialogStateMachine.clearDialogState(message.getChatId());
        } else if (message.hasText() && message.getText().equalsIgnoreCase("Другая дата")) {
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Выберете дату следуя образцу 2023-04-25"));
        } else if (message.hasText() && matchesDateOrNot(message.getText())) {
            String date = message.getText();
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Вы выбрали дату: " + date));
            userSelectedDateMap.put(message.getChatId(), date);
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Неверный формат даты. Пожалуйста, выберите из предложенных вариантов или отформатируйте вашу дату согласно образцу"));
        }
    }

    private void handleGetCommand(Message message) {
        if (message.hasText()) {
            String teamName = message.getText();
            executeMessage(sendMessageOperationCreate.getTimeTable(message, teamName, userSelectedCategoryMap, userSelectedDateMap));
            dialogStateMachine.clearDialogState(message.getChatId());
        } else {
            executeMessage(SendMessageOperationCreate.createSimpleMessage(message, "Пожалуйста, введите название команды (teamName):"));
        }
    }

    private boolean matchesDateOrNot(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            LocalDate.parse(date, formatter);
            return true;
        } catch (ExceptionHandler.DateProcessingException e) {
            return false;
        }
    }

    private boolean compareInput(String category) {
        return (category.equalsIgnoreCase("Хоккей") || category.equalsIgnoreCase("Футбол") || category.equalsIgnoreCase("Волейбол") ||
                category.equalsIgnoreCase("Баскетбол") || category.equalsIgnoreCase("Теннис") || category.equalsIgnoreCase("Гандбол"));
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



