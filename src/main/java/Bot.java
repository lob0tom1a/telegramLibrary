import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;


public class Bot extends TelegramLongPollingBot {
    private static final String TOKEN = "5918365151:AAF_5seI26ryFmt_1OIY-MbfyMGhk-HG4W0";
    private static final String USERNAME = "knig0_bot";
    String url = "jdbc:sqlite:C:\\Users\\Nikita\\Desktop\\dbsforbot\\TelegramLibrary.db";
    private String allListComm = "/view";
    private String lookforComm = "/search";
    private String binComm = "/bin";
    private String adminComm = "/admin";
    private String potentialName="";
    private String addName="";
    private String addAuthor="";
    private String addInfo="";
    private String addGenre="";
    private String totalAdress = "";
    private int totalSum = 0;
    private int addCost=0;
    private int addStock=0;
    private int potentialCost=0;
    private boolean isAdmin = false;
    private String potentialAuthor="";
    ArrayList<Integer> sums = new ArrayList<>();
    int index = 0;
    private String previousCommand = "";
    ArrayList<String> bin = new ArrayList<>();
    ArrayList<String> booklsit = new ArrayList<>();

    public Bot() throws SQLException {

    }

    private boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            String text = update.getMessage().getText();
            System.out.println(previousCommand);
            String chatId = update.getMessage().getChatId().toString();
            message.setChatId(chatId);
            if (previousCommand.equals("/view")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else if (isDigit(text)) {
                    try (Connection connection = DriverManager.getConnection(url);
                         Statement statement = connection.createStatement()) {
                        if (Integer.parseInt(text)>booklsit.size() || Integer.parseInt(text)<=0){
                            message.setText("Неверное значение! Введите число от 1 до "+booklsit.size());
                        }
                        else {
                            String name = booklsit.get(Integer.parseInt(text) - 1);
                            String sql = "SELECT * from BookInfo where `Name`='" + name + "'";
                            ResultSet resultSet = statement.executeQuery(sql);
                            while (resultSet.next()) {
                                int ID = resultSet.getInt("ID");
                                String Name = resultSet.getString("Name");
                                String Info = resultSet.getString("Info");
                                int Cost = resultSet.getInt("Cost");
                                String Author = resultSet.getString("Author");
                                String Genre = resultSet.getString("Genre");
                                potentialCost = Cost;
                                potentialAuthor = Author;
                                potentialName = Name;
                                message.setText("Название книги: '" + Name + "'\nАвтор: " + Author + "\nКороткое описание: " + Info + "\nЖанр: " + Genre + "\nЦена за штуку: " + Cost + "\n\nДобавить книгу в корзину?");
                            }
                            resultSet.close();
                            previousCommand = ".binask";
                        }
                    } catch (SQLException e) {
                        System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                    }


                }
                else {
                    message.setChatId(chatId);
                    message.setText("Неизвестная команда");
                }
            }
            else if (text.equals("/exit")){
                message.setChatId(chatId);
                message.setText("Вы не выбрали ни одно действие, чтобы его отменять");
                previousCommand="";
            }
            else if (previousCommand.equals(".binask")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else if (text.equals("Да") || text.equals("да")){
                    sums.add(potentialCost);
                    bin.add(potentialAuthor+", "+potentialName+", "+potentialCost+"руб.");
                    message.setText("Книга ("+potentialName+") успешно добавлена в корзину");
                    potentialName="";
                    potentialCost=0;
                    potentialAuthor="";
                    previousCommand="";
                }
                else if (text.equals("Нет") || text.equals("нет")){
                    message.setText("Книга ("+potentialName+") не добавлена в корзину");
                    potentialName="";
                    potentialCost=0;
                    potentialAuthor="";
                    previousCommand="";

                }
                else  message.setText("Неверная команда");

            }
            else if (text.equals(allListComm)) {
//                SendMessage message1 = new SendMessage();
//                message1.setChatId(chatId);
//                message1.setText("Q");
//                try {
//                    execute(message1); // Call method to send the message
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
                message.setChatId(chatId);
                booklsit.clear();
                if (text.equals("/exit")) {
                    message.setText("Действие отменено");
                    previousCommand = "";
                } else {
                    previousCommand = "/view";
                    String result = "";
                    try (Connection connection = DriverManager.getConnection(url);
                         Statement statement = connection.createStatement()) {
                        String sql = "SELECT * FROM BookStock";
                        ResultSet resultSet = statement.executeQuery(sql);
                        int i = 1;
                        // Обработка результатов запроса
                        while (resultSet.next()) {
                            // Получение данных из каждой строки
                            String Name = resultSet.getString("Name");
                            String Author = resultSet.getString("Author");
                            int Cost = resultSet.getInt("Cost");
                            int Stock = resultSet.getInt("Stock");
                            booklsit.add(Name);
                            result += i + ". " + Author + ", " + Name + ", " + Cost + "руб. " + Stock + "шт." + "\n";
                            i++;
                        }

                        resultSet.close();
                    } catch (SQLException e) {
                        System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                    }
                    message.setText(result + "\nВыберите книгу для просмотра");
                }
            }
            else if (previousCommand.equals(".addInfo")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    addInfo = text;
                    try (Connection connection = DriverManager.getConnection(url);
                         Statement statement = connection.createStatement()) {
                        String sql = "Insert into BookInfo(ID,Name,Info,Cost,Author,Genre) values(" + 1 + ",'" + addName + "','" + addInfo + "'," + addCost + ",'" + addAuthor + "','" + addGenre + "');" +
                                "Insert into BookStock(Name,Author,Cost,Stock) values('" + addName + "','" + addAuthor + "'," + addCost + "," + addStock + ");";
                        statement.executeUpdate(sql);
                        previousCommand = "";
                        message.setText("Книга успешно добавлена в корзину");
                    } catch (SQLException e) {
                        message.setText("Книга не добавлена в корзину(непредвиденная ошибка).");
                        previousCommand = "";
                        System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                    }
                }
            }
            else if (previousCommand.equals(".addask")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    if (text.equals("Да") || text.equals("да")) {
                        message.setText("Теперь введите краткое описание книги");
                        previousCommand = ".addInfo";
                    } else if (text.equals("Нет") || text.equals("нет")) {
                        message.setText("Книга не добавлена в базу данных");
                        previousCommand = "";
                    } else {
                        message.setText("Неверная команда. Добавление книги отменено");
                        previousCommand = "";
                    }
                }
            }
            else if (previousCommand.equals(".add")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    String[] additions = text.split(";");
                    addAuthor = additions[0];
                    addName = additions[1];
                    addGenre = additions[2];
                    addCost = Integer.parseInt(additions[3]);
                    addStock = Integer.parseInt(additions[4]);
                    message.setText("Автор - " + addAuthor + "\nНазвание - " + addName + "\nЖанр - " + addGenre + "\nЦена за штуку - " + addCost + " руб.\nКоличество копий - " + addStock + " шт.\nДобавить эту книгу?");
                    previousCommand = ".addask";
                }
            }
            else if (previousCommand.equals(".costChange")){
                message.setChatId(chatId);
                String[] needed = text.split(";");
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    if (needed.length != 2) {
                        message.setText("Вы некорректно ввели данные");
                    } else {
                        if (!isDigit(needed[1])) {
                            message.setText("Вы некорректно ввели данные");
                        } else {
                            try (Connection connection = DriverManager.getConnection(url);
                                 Statement statement = connection.createStatement()) {
                                String sql = "Update `BookInfo` set Cost=" + Integer.parseInt(needed[1]) + " where Name='" + needed[0] + "'; Update `BookStock` set Cost=" + Integer.parseInt(needed[1]) + " where Name='" + needed[0] + "'";
                                statement.executeUpdate(sql);
                                previousCommand = "";
                                message.setText("Данные успешно обновлены");
                            } catch (SQLException e) {
                                message.setText("Непредвиденная ошибка.Данные не обновлены");
                                previousCommand = "";
                                System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            else if (previousCommand.equals(".stockChange")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    String[] needed = text.split(";");
                    if (needed.length != 2) {
                        message.setText("Вы некорректно ввели данные");
                    } else {
                        if (!isDigit(needed[1])) {
                            message.setText("Вы некорректно ввели данные");
                        } else {
                            try (Connection connection = DriverManager.getConnection(url);
                                 Statement statement = connection.createStatement()) {
                                String sql = "Update `BookStock` set Stock=" + Integer.parseInt(needed[1]) + " where Name='" + needed[0] + "'";
                                statement.executeUpdate(sql);
                                previousCommand = "";
                                message.setText("Данные успешно обновлены");
                            } catch (SQLException e) {
                                message.setText("Непредвиденная ошибка.Данные не обновлены");
                                previousCommand = "";
                                System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            else if (previousCommand.equals(".statusChange")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    String[] needed = text.split(";");
                    if (needed.length != 2) {
                        message.setText("Вы некорректно ввели данные");
                    } else {
                        if (!isDigit(needed[0])) {
                            message.setText("Вы некорректно ввели данные");
                        } else {
                            try (Connection connection = DriverManager.getConnection(url);
                                 Statement statement = connection.createStatement()) {
                                String sql = "Update `Order` set Status='" + needed[1] + "' where ID=" + Integer.parseInt(needed[0]) + "";
                                statement.executeUpdate(sql);
                                previousCommand = "";
                                message.setText("Данные успешно обновлены");
                            } catch (SQLException e) {
                                message.setText("Непредвиденная ошибка.Данные не обновлены");
                                previousCommand = "";
                                System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            else if (previousCommand.equals(".changeAsk")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    //Введите название книгии измененный параметр(прим. Капитанская Дочка;12)
                    if (isDigit(text)) {
                        if (Integer.parseInt(text) == 1) {
                            message.setText("Введите название книги и измененный параметр(прим. Капитанская Дочка;12)");
                            previousCommand = ".costChange";
                        } else if (Integer.parseInt(text) == 2) {
                            message.setText("Введите название книги и измененный параметр(прим. Капитанская Дочка;12)");
                            previousCommand = ".stockChange";
                        } else if (Integer.parseInt(text) == 3) {
                            message.setText("Введите ID заказа и новый статус заказа(прим. 111;Ready)");
                            previousCommand = ".statusChange";
                        }
                    }
                }
            }
            else if (text.equals("/change")){
                message.setChatId(chatId);
                if (isAdmin){
                    previousCommand=".changeAsk";
                    message.setText("Введите какую характеристику хотите изменить: Цена за штуку(1);Количество копий(2);Статус заказа(3)");
                }
                else{
                    previousCommand="";
                    message.setText("Вы не админ!");
                }
            }
            else if (text.equals("/add")){
                message.setChatId(chatId);
                if (isAdmin){
                    previousCommand=".add";
                    message.setText("Введите данные книги в формате: Автор;Название;Жанр;Цена за штуку;Количество копий");
                }
                else{
                    previousCommand="";
                    message.setText("Вы не админ!");
                }
            }
            else if (previousCommand.equals(".admin")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    if (text.equals("admin123")) {
                        message.setText("Вы успешно вошли в систему, как админ");
                        isAdmin = true;
                        previousCommand = "";
                    } else {
                        message.setText("Неверный код");
                        previousCommand = "";
                    }
                }
            }
            else if (text.equals("/admin")){
                message.setChatId(chatId);
                message.setText("Введите специальный код:");
                previousCommand=".admin";
            }
            else if (previousCommand.equals(".status")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else if (isDigit(text)){
                    try (Connection connection = DriverManager.getConnection(url);
                         Statement statement = connection.createStatement()) {
                        String sql = "SELECT Status FROM `Order` where ID="+Integer.parseInt(text);
                        ResultSet resultSet = statement.executeQuery(sql);
                        String status = "";
                        // Обработка результатов запроса
                        while (resultSet.next()) {
                            // Получение данных из каждой строки
                           status = resultSet.getString("Status");
                        }
                        message.setText("Статус заказа - "+status);
                        previousCommand="";
                        resultSet.close();
                    } catch (SQLException e) {
                        message.setText("Такого ID не существует");
                        previousCommand = "";
                        System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                    }
                }
                else {
                    message.setText("Неверная команда");
                }
            }
            else if (text.equals("/status")){
                message.setChatId(chatId);
                message.setText("Введите ID заказа");
                previousCommand = ".status";
            }
            else if (previousCommand.equals(".payConfirm")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    Random rnd = new Random();
                    int ID = rnd.nextInt(1, 10000000 + 1);
                    if (text.equals("да") || text.equals("Да")) {
                        try (Connection connection = DriverManager.getConnection(url);
                             Statement statement = connection.createStatement()) {
                            String sql = "Insert into `Order`(Books,ID,Adress,Sum,Status) values('" + bin.toString() + "'," + ID + ",'" + totalAdress + "'," + totalSum + ",'Not Ready');";
                            statement.executeUpdate(sql);
                            bin.clear();
                            totalSum = 0;
                            totalAdress = "";
                            previousCommand = "";
                            message.setText("Заказ оформлен. Его ID - " + ID);
                        } catch (SQLException e) {
                            message.setText("Непредвиденная ошибка. Оформление заказа отменено");
                            totalSum = 0;
                            totalAdress = "";
                            previousCommand = "";
                            System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                        }
                    } else if (text.equals("Нет") || text.equals("нет")) {
                        message.setText("Оформление заказа отменено");
                        totalSum = 0;
                        totalAdress = "";
                        previousCommand = "";
                    }
                }
            }
            else if (previousCommand.equals(".adress")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    message.setText("Ваш адрес - " + text + "\nПодтверждаем заказ?");
                    totalAdress = text;
                    previousCommand = ".payConfirm";
                }
            }
            else if (previousCommand.equals(".pay")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else {
                    if (text.equals("курьер") || text.equals("Курьер")) {
                        message.setText("Доставка курьером.\nВведите адрес");
                        previousCommand = ".adress";
                    } else if (text.equals("самовывоз") || text.equals("Самовывоз")) {
                        message.setText("Доставка самовывозом. Адрес, где можно забрать заказ - ул.Пушкина, д.Колотушкина\nПодтверждаем заказ?");
                        totalAdress = "ул.Пушкина, д.Колотушкина";
                        previousCommand = ".payConfirm";
                    } else message.setText("Некорректный способ.");
                }
            }
            else if (previousCommand.equals(".bin")){
                message.setChatId(chatId);
                if (text.equals("/exit")){
                    message.setText("Действие отменено");
                    previousCommand="";
                }
                else if (isDigit(text)){
                    bin.remove(Integer.parseInt(text)-1);
                    sums.remove(Integer.parseInt(text)-1);
                    message.setText("Книга удалена");
                    previousCommand="";
                }
                else if (text.equals("/pay")){
                    int sum = 0;
                    for (int element:sums){
                        sum+=element;
                    }
                    totalSum = sum;
                    if (sum==0){
                        message.setText("Корзина пуста");
                        previousCommand="";
                    }
                    else {
                        message.setText("Всего к оплате " + sum + "\nВыберите способ доставки(курьер, самовывоз)");
                        previousCommand=".pay";
                    }
                }
                else {
                    message.setText("Неопознанная команда");
                }
            }
            else if (text.equals(binComm)){
                previousCommand=".bin";
                String result = "";
                int i = 1;
                for (String element:bin) {
                    result+=i+". "+element+"\n";
                    i++;
                }
                message.setChatId(chatId);
                message.setText("Ваша корзина:\n"+result+"\nЧтобы удалить книгу из корзины, напишите ее номер. Для оплаты напишите /pay, для очистки корзины напишите /clear, для выхода напишите /exit.");
            }
            else if (previousCommand.equals(".genre")) {
                message.setChatId(chatId);
                String result = "";
                try (Connection connection = DriverManager.getConnection(url);
                     Statement statement = connection.createStatement()) {
                    booklsit.clear();
                    String sql = "Select distinct BookStock.Name, BookStock.Author, BookStock.Cost, BookStock.Stock from BookStock, BookInfo where BookInfo.`Genre`='"+text+"' and BookInfo.Name=BookStock.Name";
                    ResultSet resultSet = statement.executeQuery(sql);
                    int i = 1;
                    // Обработка результатов запроса
                    while (resultSet.next()) {
                        String Name = resultSet.getString("Name");
                        String Author = resultSet.getString("Author");
                        int Cost = resultSet.getInt("Cost");
                        int Stock = resultSet.getInt("Stock");
                        potentialCost=Cost;
                        potentialAuthor=Author;
                        potentialName=Name;
                        booklsit.add(Name);
                        result+=i+". "+Author+", "+Name+", "+Cost+"руб. "+Stock+"шт."+"\n";
                        i++;
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                }
                if (result.equals("") || result==null){
                    message.setText("Ничего не найдено");
                    previousCommand="";
                }
                else {
                    message.setText(result + "\nВыберите книгу для просмотра");
                    previousCommand="/view";
                }
            }
            else if (previousCommand.equals(".author")) {
                String result = "";
                try (Connection connection = DriverManager.getConnection(url);
                     Statement statement = connection.createStatement()) {
                    booklsit.clear();
                    String sql = "Select distinct BookStock.Name, BookStock.Author, BookStock.Cost, BookStock.Stock from BookStock, BookInfo where BookInfo.`Author`='"+text+"' and BookInfo.Name=BookStock.Name";
                    ResultSet resultSet = statement.executeQuery(sql);
                    int i = 1;
                    // Обработка результатов запроса
                    while (resultSet.next()) {
                        String Name = resultSet.getString("Name");
                        String Author = resultSet.getString("Author");
                        int Cost = resultSet.getInt("Cost");
                        int Stock = resultSet.getInt("Stock");
                        potentialCost=Cost;
                        potentialAuthor=Author;
                        potentialName=Name;
                        booklsit.add(Name);
                        result+=i+". "+Author+", "+Name+", "+Cost+"руб. "+Stock+"шт."+"\n";
                        i++;
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                }
                message.setChatId(chatId);
                if (result.equals("") || result==null){
                    message.setText("Ничего не найдено");
                    previousCommand="";
                }
                else {
                    message.setText(result + "\nВыберите книгу для просмотра");
                    previousCommand="/view";
                }
            }
            else if (previousCommand.equals(".name")) {
                message.setChatId(chatId);
                String result = "";
                try (Connection connection = DriverManager.getConnection(url);
                     Statement statement = connection.createStatement()) {
                    booklsit.clear();
                    String sql = "Select distinct BookStock.Name, BookStock.Author, BookStock.Cost, BookStock.Stock from BookStock, BookInfo where BookInfo.`Name`='"+text+"' and BookInfo.Name=BookStock.Name";
                    ResultSet resultSet = statement.executeQuery(sql);
                    int i = 1;
                    // Обработка результатов запроса
                    while (resultSet.next()) {
                        String Name = resultSet.getString("Name");
                        String Author = resultSet.getString("Author");
                        int Cost = resultSet.getInt("Cost");
                        int Stock = resultSet.getInt("Stock");
                        potentialCost=Cost;
                        potentialAuthor=Author;
                        potentialName=Name;
                        booklsit.add(Name);
                        result+=i+". "+Author+", "+Name+", "+Cost+"руб. "+Stock+"шт."+"\n";
                        i++;
                    }
                    resultSet.close();
                } catch (SQLException e) {
                    System.out.println("Ошибка при выполнении запроса: " + e.getMessage());
                }
                if (result.equals("") || result==null){
                    message.setText("Ничего не найдено");
                    previousCommand="";
                }
                else {
                    message.setText(result + "\nВыберите книгу для просмотра");
                    previousCommand="/view";
                }

            }
            else if (previousCommand.equals(lookforComm)){
                message.setChatId(chatId);
                if (isDigit(text) && Integer.parseInt(text)>0 && Integer.parseInt(text)<=4){
                    if (Integer.parseInt(text)==1) {
                        message.setText("Введи жанр произведения:");
                        previousCommand = ".genre";
                    }
                    else if (Integer.parseInt(text)==2) {
                        message.setText("Введи ФИО автора:");
                        previousCommand = ".author";
                    }
                    else if (Integer.parseInt(text)==3) {
                        message.setText("Введи название книги:");
                        previousCommand = ".name";
                    }
                    else {
                        message.setText("Неверная команда");
                    }
                }
                else message.setText("Неверная команда");
            }
            else if (text.equals("/exit")){
                previousCommand="";
            }
            else if (text.equals(lookforComm)){
                previousCommand="/search";
                message.setChatId(chatId);
                message.setText("Выбери критерий:\n1.Жанр\n2.Автор\n3.Название\n");
            }

            else if (text.equals("/start")){
                message.setChatId(chatId);
                message.setText("Приветствую! Я бот, который поможет вам сделать заказ из нашего книжного магазина!\nЧтобы вы могли сориентироваться, вот список команд, которые я поддерживаю:\n"
                        +"1./view - посмотреть все доступные на складе книги\n2./search - поискать книгу по определенной характеристике\n3./bin - посмотреть вашу корзину\n4./status - посмотреть статус заказа"
                        +"\n5./exit - отменить действие\nНадеюсь, вы найдете себе подходящую книгу! Желаю удачи!");
                previousCommand="";
            }
            else {
                previousCommand = "";
                message.setChatId(chatId);
                message.setText("Неверная команда");
            }
//            if (previousCommand.equals("")){
//                SendMessage message1 = new SendMessage();
//                message1.setChatId(chatId);
//                message1.setText("Что желаете сделать?");
//                try {
//                    execute(message1); // Call method to send the message
//                } catch (TelegramApiException e) {
//                    e.printStackTrace();
//                }
//            }
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }
}
