--
-- Файл сгенерирован с помощью SQLiteStudio v3.4.4 в Вт май 23 10:46:04 2023
--
-- Использованная кодировка текста: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Таблица: BookStock
CREATE TABLE IF NOT EXISTS `BookStock` (
  `Name` text NOT NULL,
  `Author` text NOT NULL,
  `Cost` int(10) NOT NULL,
  `Stock` int(10) NOT NULL
);
INSERT INTO BookStock (Name, Author, Cost, Stock) VALUES ('Капитанская Дочка', 'А.С. Пушкин', 250, 230);
INSERT INTO BookStock (Name, Author, Cost, Stock) VALUES ('Тихий Дон', 'М.А. Шолохов', 400, 277);
INSERT INTO BookStock (Name, Author, Cost, Stock) VALUES ('Автостопом по Галактике', 'Д. Адамс', 600, 435);
INSERT INTO BookStock (Name, Author, Cost, Stock) VALUES ('Метро 2033', 'Д.А. Глуховский', 790, 201);

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
