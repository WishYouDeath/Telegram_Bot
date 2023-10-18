import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static constant.Commands.*;
public class MyTelegramBot extends TelegramLongPollingBot {
    SendMessageOperationCreate sendMessageOperationCreate = new SendMessageOperationCreate();
    Map<String, Consumer<Message>> commandMap = new HashMap<>();
    public void addCommands(){
        commandMap.put(START, message -> executeMessage(sendMessageOperationCreate.createGreetingInformation(message)));
        commandMap.put(HELP, message -> executeMessage(sendMessageOperationCreate.createHelpInformation(message)));
        commandMap.put(ABOUT, message -> executeMessage(sendMessageOperationCreate.createBotInformation(message)));
        commandMap.put(AUTHORS, message -> executeMessage(sendMessageOperationCreate.createAuthorsInformation(message)));
        commandMap.put(GET, message -> handleGetCommand(message));
    }
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            handleMessage(update.getMessage());
        }
    }
    private void handleMessage(Message message) {
        addCommands();
        if (message.hasText() && message.hasEntities()) {
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();

            if (commandEntity.isPresent()) {
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                commandMap.getOrDefault(command, msg -> executeMessage(sendMessageOperationCreate.wrongCommand(message))).accept(message);
            }
        }
    }

    private void handleGetCommand(Message message) {
        // Ожидаем следующее сообщение пользователя с именем команды (teamName)
        // Для этого нужно проверить, есть ли следующее сообщение и является ли оно текстовым
        System.out.println("yes");
        if (message.getReplyToMessage() != null && message.getReplyToMessage().hasText()) {
            String teamName = message.getReplyToMessage().getText(); // Получение teamName
            System.out.println(teamName);
            Parser parser = new Parser();
            String timetable = parser.receiveData(teamName); // Получить результат парсинга

            // Теперь отправьте результат парсинга через SendMessageOperationCreate
            executeMessage(sendMessageOperationCreate.getTimeTable(message, timetable));
        } else {
            executeMessage(sendMessageOperationCreate.createSimpleMessage(message, "Пожалуйста, введите название команды (teamName):"));
        }
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
    private <T extends BotApiMethod> void executeMessage(T sendMessage){
         try {
             execute(sendMessage);
         } catch (TelegramApiException e) {
             System.out.println("Не удалось отправить сообщение" + e.getCause());
         }
     }
}

