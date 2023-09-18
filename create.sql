create table atividade (id number(19,0) generated as identity, atividade varchar2(255 char), created_at date, data_dia date not null, duracao number(10,0) not null check (duracao>=0), lembrete_id number(19,0), primary key (id));
create table lembrete (id number(19,0) generated as identity, duracao number(10,0) not null check (duracao>=0), mensagem varchar2(255 char), primary key (id));
create table usuario (id number(19,0) generated as identity, email varchar2(255 char), nome varchar2(255 char), senha varchar2(255 char), primary key (id));
alter table atividade add constraint FKiokrdno1p36i66q6epw7qf94t foreign key (lembrete_id) references lembrete;
