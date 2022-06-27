DELETE FROM user_roles;
DELETE FROM meals;
DELETE FROM users;
ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_roles (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, datetime, description, calories)
VALUES (100000, '2022-06-20 10:00:00', 'Завтрак юзера', 600),
       (100000, '2022-06-20 15:00:00', 'Обед юзера', 1300),
       (100000, '2022-06-21 20:00:00', 'Ужин юзера', 700),
       (100001, '2022-06-20 10:00:00', 'Завтрак админа', 500),
       (100001, '2022-06-20 21:00:00', 'Ужин админа', 800);
