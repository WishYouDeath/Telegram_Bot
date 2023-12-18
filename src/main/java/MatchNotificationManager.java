//import constant.NotificationType;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//
//public class MatchNotificationManager {
//    private final Map<Long, Map<String, NotificationType>> matchNotifications = new HashMap<>();
//    private final MyTelegramBot bot;
//
//
//    public MatchNotificationManager(MyTelegramBot bot) {
//        this.bot = bot;
//        startNotificationScheduler();
//    }
//
//
//    public void setMatchNotification(Long chatId, String teamName, NotificationType notificationType) {
//        matchNotifications
//                .computeIfAbsent(chatId, k -> new HashMap<>())
//                .put(teamName, notificationType);
//    }
//
//    public void clearMatchNotification(Long chatId, String teamName) {
//        matchNotifications
//                .computeIfAbsent(chatId, k -> new HashMap<>())
//                .remove(teamName);
//    }
//
//    public void checkAndSendNotifications() {
//        iterateOverChatsAndSendNotifications();
//    }
//
//    private void iterateOverChatsAndSendNotifications() {
//        for (Map.Entry<Long, Map<String, NotificationType>> entry : matchNotifications.entrySet()) {
//            Long chatId = entry.getKey();
//            Map<String, NotificationType> teamNotifications = entry.getValue();
//
//            for (Map.Entry<String, NotificationType> teamEntry : teamNotifications.entrySet()) {
//                String teamName = teamEntry.getKey();
//                NotificationType notificationType = teamEntry.getValue();
//
//                if (isTimeForNotification(teamName, notificationType)) {
//                    bot.setMatchNotification(chatId, teamName, notificationType);
//                }
//            }
//        }
//    }
//
//    private boolean isTimeForNotification(String teamName, NotificationType notificationType) {
//        // Реализуйте логику проверки времени уведомления
//        return true;  // Замените это условие на вашу логику
//    }
//
//    private void startNotificationScheduler() {
//        ScheduledExecutorService notificationScheduler = Executors.newSingleThreadScheduledExecutor();
//        notificationScheduler.scheduleAtFixedRate(this::checkAndSendNotifications, 0, 1, TimeUnit.MINUTES);
//    }
//}
