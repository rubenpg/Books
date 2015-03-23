source bookdb-schema.sql;
insert into users values('ruben', MD5('ruben'), 'Ruben', 'ruben@acme.com');
insert into user_roles values ('ruben', 'registered');

insert into users values('marc', MD5('marc'), 'Marc', 'marc@acme.com');
insert into user_roles values ('marc', 'registered');

insert into users values('isra', MD5('isra'), 'Isra', 'isra@acme.com');
insert into user_roles values ('isra', 'registered');

insert into authors (name) values ("Ruben Muchaberga");
insert into authors (name) values ("Pau Mamano");
insert into authors (name) values ("Koushun Takami");
insert into authors (name) values ("Stephen King");
insert into authors (name) values ("Tipler");
insert into authors (name) values ("Agatha Christie");

insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values ("La vida","Ruben Muchaberga","espaniol","32","dia1","dia12","lamasfamosa");
insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values ("LoL para noobs","Pau Mamano","paletodepueblo","1","dia2","dia22","impresaengaraje");
insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values ("Battle Royale","Koushun Takami","japones","5","dia3","dia32","boocket");
insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values ("Apocalipsis","Stephen King","ingles","7","dia4","dia42","debolsillo");
insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values ("Libro de fisica","Tipler","matematico","9","dia5","dia52","nomeacuerdo");
insert into books (title, author, language, edition, editiondate, impresiondate, editorial) values ("Diez negritos","Agatha Christie","ingles","99","dia6","dia62","clubdelectores");

insert into reviews (username, name, bookid, content) values ("ruben","ruben",1,"el libro de mi caravida");
insert into reviews (username, name, bookid, content) values ("marc","marc",2,"libro para tontos, empieza: el mundo fue creado por dioooooh");
insert into reviews (username, name, bookid, content) values ("ruben","ruben",3,"libraso");
insert into reviews (username, name, bookid, content) values ("ruben","ruben",4,"otro libraso");
insert into reviews (username, name, bookid, content) values ("isra","isra",5,"libraso");
insert into reviews (username, name, bookid, content) values ("ruben","ruben",6,"la mejor novela de asesinatos de la jistory");