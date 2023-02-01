create database todolist;

create table user(
	id varchar(5) primary key,
	user_name varchar(100) not null,
	password varchar(100) not null,
	email varchar(100) unique
);

create table todos(
	id varchar(7) primary key,
	description varchar(100) not null,
	user_id varchar(5) not null,
	constraint foreign key(user_id)references user(id) 
);