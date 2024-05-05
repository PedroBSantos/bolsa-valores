create table compras(id varchar(36) not null primary key, codigo_acao varchar(6) not null, data_compra varchar(10), valor_acao numeric not null, quantidade int4 not null);

create table proventos(id varchar(36) not null primary key, codigo_acao varchar(6) not null, data_pagamento varchar(10), tipo varchar(9) not null, valor numeric not null);