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



