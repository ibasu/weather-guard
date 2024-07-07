create table IF NOT EXISTS WEATHER(
 id BIGINT not null,
 main varchar(1000) not null,
 description varchar(4000) not null,
 icon varchar(100) ,
 created_date_time timestamp,
 last_updated_date_time timestamp,
 PRIMARY KEY (id)
);

