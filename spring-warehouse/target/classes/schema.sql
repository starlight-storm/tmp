DROP TABLE GOODS if exists
CREATE TABLE GOODS(ID INTEGER IDENTITY PRIMARY KEY,CODE INTEGER,NAME VARCHAR(20) NOT NULL, PRICE INTEGER, STATUS VARCHAR(10) NOT NULL)
ALTER TABLE GOODS ADD CONSTRAINT UNQGOODS UNIQUE(CODE)