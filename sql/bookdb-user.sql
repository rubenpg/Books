drop user 'book'@'localhost';
create user 'book'@'localhost' identified by 'book';
grant all privileges on bookdb.* to 'book'@'localhost';
flush privileges;