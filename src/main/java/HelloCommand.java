//import org.telegram.telegrambots.bots.TelegramLongPollingBot;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//import org.telegram.telegrambots.meta.api.objects.Update;
//import org.telegram.telegrambots.meta.bots.AbsSender;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.meta.generics.TelegramBot;
//
//import java.util.List;
//import java.util.Queue;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.TimeUnit;
//
//public class HelloCommand implements Runnable {
//    private long chatId;
//    private final TelegramLongPollingBot bot;
//
//    public HelloCommand(long chatId, TelegramLongPollingBot bot) {
//        this.chatId = chatId;
//        this.bot = bot;
//    }
//
//    @Override
//    public void run() {
//        SendMessage message = new SendMessage(chatId, "Hello! Please enter a number:");
//        try {
//            bot.execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//
//        int number = 0;
//        try {
//            number = waitForNumber();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        message.setText("Your number doubled is: " + (number * 2));
//        try {
//            bot.execute(message);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private int waitForNumber() throws InterruptedException {
//        Queue<Update> updates = new ConcurrentLinkedQueue<>();
//        bot.execute(new SendMessage(chatId, "Please enter a number:"));
//        while (true) {
//            for (Update update : updates) {
//                if (update.hasMessage() && update.getMessage().hasText()) {
//                    String text = update.getMessage().getText();
//                    try {
//                        int number = Integer.parseInt(text);
//                        return number;
//                    } catch (NumberFormatException ignored) {
//                    }
//                }
//            }
//            Thread.sleep(1000);
//            updates.addAll(bot.getUpdates(updates.isEmpty() ? null : updates.peek().getUpdateId() + 1, 0, 0));
//        }
//    }
//}
