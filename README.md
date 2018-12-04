# some notes on implementation
## architecture and components
- Spark for REST API - I used only spring in the past, so I googled and pick the one that looked simpliest
- Guice for IoC/DI - same story, picked the most known, since my only experience was with Spring
- Ormlite - I had experience with Hibernate, but was looking for smth simple and easy-to-use
- h2 - just in-memory db for faster usage
## implementation overview
- mivanov/rev/AppModule.java - Guice config
- mivanov/rev/api/Router.java - configures www routing, start/stop www layer, create tables for DB
- mivanov/rev/api/model - REST contracts and util functions for contracts
- mivanov/rev/logic - business logic controllers
- mivanov/rev/logic/status - statuses of business operations, mapped to REST API return codes
- mivanov/rev/model - data model
- mivanov/rev/api - integration tests and utils
## implementation details
- router and jdbc connection source are singletons. Router - just because it's kind-of single entry-point. 
  JDDB pooled connection - if I did production system, I would spent more time on this topic
- controllers are handling logic, including db requests. I though it's boring to keep balance per user as one row (and won't massive load),
  and tried to implement it as a first approach to big load. I.e. I handle balance as a set of balance buckets. When user topup his balance
  (or get money transfer), it just add another bucket, inserting row in db (so this part of operation is lock free). When user deduct 
  money we try to deduct this amount from any of his buckets and only in case there is no single bucket with enough amount, we start
  transaction to merge his buckets into one and try to deduct. 
  Again - it's not perfect, but it's better then to lock balance each time. In reality I would spend more time and do proper load
  testing of implemented approach.
- API itself - it's kind of okay. In reality I would not use user performing operation as a parameter, since it should be some 
  authentication token.
## summary
it was honest 8 hours I think (I don't count time I spent thinking on a task in tube:)). If I would have more time, I did load testing, refactor API itself. Ideally I would split front-end and business logic by layers, adding queues between them. And many more:) But okay, it was 8 hours exercise
