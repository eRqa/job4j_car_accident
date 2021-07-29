CREATE TABLE type
(
    id serial primary key,
    name varchar(2000)
);

CREATE TABLE accident
(
    id serial primary key,
    name varchar(2000),
    text text,
    address text,
    type_id integer REFERENCES type (Id)
);

CREATE TABLE rule
(
    id serial primary key,
    name varchar(2000)
);

CREATE TABLE accident_rule
(
    id serial primary key,
    accident_id integer references accident (id),
    rule_id  integer references rule (id)
);

INSERT INTO type(name)
VALUES ('Две машины');
INSERT INTO type(name)
VALUES ('Машина и человек');
INSERT INTO type(name)
VALUES ('Машина и велосипед');

INSERT INTO rule(name)
VALUES ('Статья. 1');
INSERT INTO rule(name)
VALUES ('Статья. 2');
INSERT INTO rule(name)
VALUES ('Статья. 3');