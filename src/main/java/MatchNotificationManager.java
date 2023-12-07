import constant.NotificationType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import static constant.NotificationType.*;

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
