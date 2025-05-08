# bolsa-valores

API REST para registrar compras de ações da bolsa de valores e o recebimento de proventos (dividendo e JCP).

## Testes de unidade e de integração

Para a execução dos testes de integração é necessário que o docker esteja instalado e em execução.
lein test para executar todos os testes

## Execução no Docker

- docker compose -f docker-compose.yml -p bolsa-valores up -d
