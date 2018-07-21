
    alter table User_address 
       drop constraint FKrhvkf3vymyf4jkt5uasg03t0i;

    drop table User if exists;

    drop table User_address if exists;

    drop sequence SEQ_USER if exists;

    drop sequence SEQ_USER_ADDRESS if exists;
create sequence SEQ_USER start with 1 increment by 100;
create sequence SEQ_USER_ADDRESS start with 1 increment by 100;

    create table User (
       id bigint not null,
        date_of_birth date,
        creation_date timestamp,
        email_address varchar(255),
        first_name varchar(255),
        last_name varchar(255),
        middle_name varchar(255),
        password varchar(255),
        status char(255),
        user_name varchar(255),
        primary key (id)
    );

    create table User_address (
       id integer not null,
        address1 varchar(255),
        address2 varchar(255),
        address3 varchar(255),
        city varchar(255),
        country varchar(255),
        postalCode varchar(255),
        province varchar(255),
        status char(255),
        type varchar(255),
        userId bigint,
        primary key (id)
    );

    alter table User_address 
       add constraint FKrhvkf3vymyf4jkt5uasg03t0i 
       foreign key (userId) 
       references User;
