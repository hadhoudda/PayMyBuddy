----
----  données pour la table `paymybuddy_test_db`.`users`
----
DELETE FROM users;
INSERT INTO users (id_user, user_name, email, password, solde, date_create) VALUES
(1, 'user1', 'user1@yahoo.fr', '$2a$10$bMajdoLBB9DpAHmUPwpWouEysYZoLbK5NPHVwB5d1Zk27.RNbNs8K', 0.00, CURRENT_TIMESTAMP),
(2, 'user2', 'user2@yahoo.fr', '$2a$10$bMajdoLBB9DpAHmUPwpWouEysYZoLbK5NPHVwB5d1Zk27.RNbNs8K', 0.00, CURRENT_TIMESTAMP);
--password : user12