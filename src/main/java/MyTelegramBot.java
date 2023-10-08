import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.Update;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static constant.Commands.*;

public class MyTelegramBot extends TelegramLongPollingBot {
    SendMessageOperationCreate sendMessageOperationCreate = new SendMessageOperationCreate();
    private Map<String, Command> hashCommands = new HashMap<>();
    String nameBot = System.getenv("Telegram_Name");
    String apiBot = System.getenv("Telegram_API");
    public MyTelegramBot() {
        // Регистрируем команды
        hashCommands.put("/help", new HelpCommand());
        hashCommands.put("/authors", new AuthorsCommand());

        // Добавить другие команды по аналогии
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            handleMessage(update.getMessage());
        }
    }

    private void handleMessage(Message message) {
        if(message.hasText() && message.hasEntities()){
            Optional<MessageEntity> commandEntity =
                    message.getEntities().stream().filter(e -> "bot_command".equals(e.getType())).findFirst();
            //Если написали команду /start 123 то мы обрезаем 123 до команды /start и так с каждой командой
            if(commandEntity.isPresent()){
                String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
                switch (command) {
                    case START:
                        executeMessage(sendMessageOperationCreate.createGreetingInformation(message));
                        break;
                    case HELP:
                        executeMessage(sendMessageOperationCreate.createHelpInformation(message));
                        break;
                    case ABOUT:
                        executeMessage(sendMessageOperationCreate.createBotInformation(message));
                        break;
                    case AUTHORS:
                        executeMessage(sendMessageOperationCreate.createAuthorsInformation(message));
                        break;
                    default:
                        executeMessage(sendMessageOperationCreate.wrongCommand(message));
                        break;
                }
            }
        }
    }
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

