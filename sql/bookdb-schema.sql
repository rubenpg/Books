drop database if exists bookdb;
create database bookdb;
 
use bookdb;
 
create table authors (
	name		varchar(30) not null primary key
);

create table books (
	bookid		int not null auto_increment primary key,
	title		varchar(100) not null,
	author		varchar(60) not null,
	language	varchar(20) not null,
	edition		varchar(70) not null,
	editiondate	varchar(30) not null,
	impresiondate	varchar(30) not null,
	editorial	varchar(30) not null,
	foreign key(author) references authors(name)
);

create table users (
	username	varchar(20) not null primary key,
	userpass	char(32) not null,
	name		varchar(70) not null,
	email		varchar(255) not null
);
 
create table user_roles (
	username	varchar(20) not null,
	rolename 	varchar(20) not null,
	foreign key(username) references users(username) on delete cascade,
	primary key (username, rolename)
);
 
create table reviews (
	reviewid 		int not null auto_increment primary key,
	username 		varchar(20) not null,
	name			varchar (70) not null,
	bookid			int not null,
	content			varchar(500) not null,
	last_modified		timestamp default current_timestamp ON UPDATE CURRENT_TIMESTAMP,
	creation_timestamp	datetime not null default current_timestamp,
	foreign key(username) 	references users(username),
	foreign key(bookid) 	references books(bookid) on delete cascade
);