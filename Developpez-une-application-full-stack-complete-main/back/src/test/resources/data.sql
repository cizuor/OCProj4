INSERT INTO topics (name, description) VALUES ('Java', 'Le langage Java');
INSERT INTO topics (name, description) VALUES ('Angular', 'Le framework Front');
INSERT INTO topics (name, description) VALUES ('C#', 'Le langage C#');
INSERT INTO topics (name, description) VALUES ('Java spring boot', 'Le framework Front');

insert into users (pseudo, email, password, created_at) values ('test','test@test.fr','$2a$10$74fGb2KaJo.bMdh1e7EjXu1dwMX/IneVixUluLEmpnL9sLwa9lK/m',CURRENT_TIMESTAMP);


insert into posts (author_id, topic_id,titre,contenu, created_at ) values ( 1,1,'java pour les null', 'java est moins bien que le C amateur',CURRENT_TIMESTAMP);
insert into posts (author_id, topic_id,titre,contenu, created_at ) values ( 1,2,'ihm != developpement', 'angular est du html deguiser go assembleur',CURRENT_TIMESTAMP);	

insert into comments (author_id,created_at,post_id,contenu) values (1,CURRENT_TIMESTAMP,1,'ou pour les vrais mec assembleur');

insert into user_subscriptions (topic_id,user_id) values (1,1);