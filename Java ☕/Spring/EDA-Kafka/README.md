# Event Driven Architecture (using apache kafka)

<!-- TOC -->
* [Event Driven Architecture (using apache kafka)](#event-driven-architecture-using-apache-kafka)
  * [What is event-driven architecture?](#what-is-event-driven-architecture)
  * [Overview of EDA](#overview-of-eda)
  * [What are the advantages of EDA?](#what-are-the-advantages-of-eda)
  * [Kafka Ecosystem](#kafka-ecosystem)
  * [Kafka Setup](#kafka-setup)
  * [Kafka Producer](#kafka-producer)
    * [Configure Kafka Producer In OrderService](#configure-kafka-producer-in-orderservice)
    * [Let's Create **Kafka Topic**](#lets-create-kafka-topic)
    * [Let's Create **Kafka Producer**](#lets-create-kafka-producer)
    * [Let's Create **REST API For Kafka Producer**](#lets-create-rest-api-for-kafka-producer)
  * [Kafka Consumer](#kafka-consumer)
    * [Configure Kafka Consumer In StockService](#configure-kafka-consumer-in-stockservice)
    * [Let's Create **Kafka Consumer**](#lets-create-kafka-consumer)
<!-- TOC -->

## What is event-driven architecture?

Event-driven architecture (EDA) is a software design pattern in which
decoupled applications can **asynchronously** publish and subscribe to events
via an event/message broker.

In an Event-Driven Architecture, applications communicate with each other
by sending and/or receiving events or messages Event-driven architecture is often referred to as "asynchronous"
communication.

Event-driven apps can be created in any programming language because event-driven is a programming approach, not a
language.

An event-driven architecture is loosely coupled.

## Overview of EDA

<p align="center">
  <img src="images/Image_001.jpg" alt="eda overview" style="width: 700px"/>
</p>

## What are the advantages of EDA?

- Improved flexibility and maintainability.
- High scalability : we can easily add more microservices.
- Improved availability : if one microservice goes down, it won't affect others.

## Kafka Ecosystem

<p align="center">
    <img src="images/Image_002.jpg" alt="ecosystem"/>
</p>

Let's break the image:

* ZooKeeper : it is a service that manages the state of all the brokers, also the topics, producers and consumers.

  > So remember to start the zookeeper to be able to manage the kafka.

## Kafka Setup

First download the zip file and extract in the base directory of your C drive (windows).

Open terminal inside the extracted kafka folder and run these commands in separate terminals:

> 💡 Run these commands in order; the zookeeper needs to be run first!

```bash
bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
 
bin\windows\kafka-server-start.bat .\config\server.properties
```

## Kafka Producer

We'll take this as our project :
<p align="center">
    <img src="images/Image_001.jpg" alt="" width="700px">
</p>

### Configure Kafka Producer In [OrderService](code_source/OrderService/src/)

The code source : [application.yaml](code_source/OrderService/src/main/resources/application.yaml)

```yaml
#kafka producer
spring:
  kafka:
    #bootstrap-servers: localhost:9092
    producer:
      bootstrap-servers: localhost:9092

      #configure serialize classes for key & value pair *
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer

    topic: #this is a custom-made key
      name: order_topics
```

> 💡 We need to configure serializers for key and values; whenever the producer sends an event,
> that event contains the data as key-value pair, and we need to configure the serializers for them.

### Let's Create **Kafka Topic**

The code
source : [KafkaTopicConfig](code_source/OrderService/src/main/java/com/example/orderservice/config/KafkaTopicConfig.java)

Firstly we need to retrieve the topic name, and then create a `NewTopic` bean and a build a topic using `TopicBuilder`.

### Let's Create **Kafka Producer**

The code
source : [OrderProducer](code_source/OrderService/src/main/java/com/example/orderservice/kafka/OrderProducer.java)

> 💡 So overall the steps is :
> 1. Create a msg using `NewTopic` bean (that we configured earlier) & `Message` (using `MessageBuilder`).
> 2. Send the msg to the kafka topic using `KafkaTemplate`.

### Let's Create **REST API For Kafka Producer**

The code
source : [OrderController](code_source/OrderService/src/main/java/com/example/orderservice/controller/OrderController.java)

> 💡 We're simply creating
> an [OrderEvent](code_source/SharedLib/src/main/java/com/example/sharedlib/model/dto/OrderEvent.java) which is a simple
> pojo class, and sending it to the kafka topic.

## Kafka Consumer

StockService & EmailService are our consumers.

### Configure Kafka Consumer In [StockService](./code_source/StockService/src/main)

The code source : [application.yaml](./code_source/StockService/src/main/resources/application.yaml)

```yaml
#kafka consumer
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: stock   #1*
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        spring:
          json:
            trusted:
              packages: "*"   #2*
    topic:
      name: order_topics   #3*
```

> 💡
>
> 1* : This property is very important; since we have multiple consumers
> (stock and email services consuming from order through kafka) from single topic,
> we need to assign IDs for kafka to recognize them.
>
> 2* : We're telling kafka to trust and deserialize all the packages in the project.
>
> 3* : It is and should be the same name as the one we mentioned in [OrderService#application.yaml_line15](./code_source/OrderService/src/main/resources/application.yaml)

### Let's Create **Kafka Consumer**

The code source : [OrderConsumer](./code_source/StockService/src/main/java/com/example/stockservice/kafka/OrderConsumer.java)

> 💡 And Done! Now if you check your logs, you can see that you have the sent 
> data from the order service!
