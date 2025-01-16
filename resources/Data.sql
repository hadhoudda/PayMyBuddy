/* Setting up paymybuddy DB */
DROP DATABASE IF EXISTS paymybuddy;
create database paymybuddy;
use paymybuddy;

DROP TABLE IF EXISTS `user`;
CREATE TABLE user (
    id_user INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_name VARCHAR(50),
    email  VARCHAR(100),
    password VARCHAR (20)
);

DROP TABLE IF EXISTS `connection`;
CREATE TABLE connection(
    id_connection INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL, -- Foreign key for logged in user
    FOREIGN KEY (id_user) REFERENCES user(id_user) ON DELETE CASCADE
);

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE transaction(
    id_transaction INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    description  VARCHAR(255),
    amount DOUBLE,
    sender_id INT NOT NULL, -- Foreign key for user sending money
    receiver_id INT NOT NULL, -- Foreign key for user receiving money
    FOREIGN KEY (sender_id) REFERENCES user(id_user) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES user(id_user) ON DELETE CASCADE
);