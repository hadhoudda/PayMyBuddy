----
---- Dumping data for table `paymybuddy_test_db`.`users`
----
DELETE FROM users;
INSERT INTO users (user_name, email, password, solde, date_create) VALUES
('user1', 'user1@yahoo.fr', 'user_1', 0.00, CURRENT_TIMESTAMP),
('user2', 'user2@yahoo.fr', 'user_2', 0.00, CURRENT_TIMESTAMP);
