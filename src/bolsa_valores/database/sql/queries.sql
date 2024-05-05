-- A :result value of :n below will return affected rows:
-- :name sql-insert-compra :! :m
-- :doc Persist a compra on database
insert into compras (id, codigo_acao, data_compra, valor_acao, quantidade) 
values (:id, :codigo-acao, :data-compra, :valor-acao, :quantidade)

-- A :result value of :n below will return affected rows:
-- :name sql-insert-provento :! :m
-- :doc Persist a provento on database
insert into proventos (id, codigo_acao, data_pagamento, tipo, valor) 
values (:id, :codigo-acao, :data-pagamento, :tipo, :valor)

-- A :result value of :n below will return affected rows:
-- :name sql-read-all-compras :? :*
-- :doc Return all compras
select id, codigo_acao as "codigo-acao", data_compra as "data-compra", valor_acao as "valor-acao", quantidade from compras

-- A :result value of :n below will return affected rows:
-- :name sql-read-all-proventos :? :*
-- :doc Return all proventos
select id, codigo_acao as "codigo-acao", data_pagamento as "data-pagamento", tipo, valor from proventos