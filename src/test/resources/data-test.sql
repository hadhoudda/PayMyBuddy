----
---- Dumping data for table `paymybuddy_test_db`.`users`
----
DELETE FROM users;
INSERT INTO users (user_name, email, password, solde, date_create) VALUES
('user1', 'user1@yahoo.fr', '$2a$10$Dow1FGXhZYqRfMFCFMSI8uD3QZ2IV1P1EKZRGzqGp4qMvjZxo85bG', 0.00, CURRENT_TIMESTAMP),
('user2', 'user2@yahoo.fr', '$2a$10$Z9uFbd6/p0yxRvuqDrYu1u5tbBD6KmkuhtR2ifkS9owxOYbWvHcPe', 0.00, CURRENT_TIMESTAMP);
