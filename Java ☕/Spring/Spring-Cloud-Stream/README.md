# Spring Cloud Stream

<!-- TOC -->
* [Spring Cloud Stream](#spring-cloud-stream)
  * [Official Reference](#official-reference)
  * [What is Spring Cloud Stream?](#what-is-spring-cloud-stream)
  * [Use-case](#use-case)
  * [The Code Sources](#the-code-sources)
    * [Spring Boot 2.7.x 🍃](#spring-boot-27x-)
    * [Spring Boot 3.x 🍃](#spring-boot-3x-)
    * [Spring Boot 3.x - Project With Spring Cloud Stream 🍃](#spring-boot-3x---project-with-spring-cloud-stream-)
<!-- TOC -->

## Official Reference

[Spring Cloud Stream Official Documentation.](https://spring.io/projects/spring-cloud-stream/#overview)

## What is Spring Cloud Stream?

Well based on official documentation :
> Spring Cloud Stream is a framework for building highly scalable event-driven microservices connected with shared
> messaging systems.

> 💡 Based on what I understood from ChatGPT and watching video :
>
> It is a framework for simplifying the use of message brokers!

## Use-case

Let's understand its use-case with this image :

<p align="center">
    <img src="./images/Screenshot 2023-10-20 161934.png" alt="spring cloud stream use-case" width="700px">
</p>

Supposing we have an event-driven architecture microservice project. Suppose today we're using _Kafka_, so
we'll add the configurations in the producers and consumers of kafka, obviously.

Now what if we decided to change our message broker in the future? What if we want to use _RabbitMQ_?
We'll have to change the configurations in the producers & consumers! It is not very efficient right? My application
is tightly coupled with the messaging system right?

|=> This is where Spring Cloud Stream helps by taking the responsibility of specifying the implementation details of the
messaging system; We only need to specify the required binding dependency (which message broker I'm using), and the rest
will be taken care by SCS.

## The Code Sources

The dependencies that we used :

- Spring web
- Spring apache kafka
- Spring cloud stream
- Lombok

### Spring Boot 2.7.x 🍃

The code source : [producer-cloud-stream](code_sources/spring-boot-2x/spring-cloud-stream-producer)

The code source : [consumer-cloud-stream](code_sources/spring-boot-2x/spring-cloud-stream-consumer)

<br/>

### Spring Boot 3.x 🍃

The code source : [kafka-binder-sb3x](./code_sources/spring-boot-3x/kafka-binder-sb3/src/main)

There are a few explanations to be made.

1. We use `Supplier`, `Consumer` and `Function` of java
   util to produce, consume & produce and consume respectively.
2. We need to specify the name of the bindings of the above methods
   in a configuration file (yaml) otherwise it'll give a default name
   which is the method name and its type with its partition :

    - If `Supplier<some_class>` : _methodName-out-0_ will be its name where out indicates
      the **producer** type.
    - If `Function<input, output>` : _methodName-in-0_ and _methodName-out-0_ where in and out
      indicates the **consumer** & **producer** type.
    - If `Consumer<some_class>` : _methodName-in-0_ where in indicates the **consumer** type.

The configuration
file : [application.yaml](./code_sources/spring-boot-3x/kafka-binder-sb3/src/main/resources/application.yaml).

or simply look the below configs :

```yaml
spring:
  cloud:
    function:
      definition: consumerBinding;processorBinding;producerBinding #any_name_can_be_given
    stream: #map its naming convention to desired topic
      bindings:
        #we're telling the kafka binders that
        #don't created default topic with `consumerBinding-in-0` name instead,
        #use the destination name that I provided
        producerBinding-out-0:
          destination: processor-desired-topic
        consumerBinding-in-0:
          destination: consumer-desired-topic
        processorBinding-in-0:
          destination: processor-desired-topic
        processorBinding-out-0:
          destination: consumer-desired-topic
      kafka:
        binder:
          brokers: localhost:9092 #it'll work even without this since it's default value is `localhost:9092`
```

<br/>

### Spring Boot 3.x - Project With Spring Cloud Stream 🍃

The code source : [web-domain-service](code_sources/sb3x-project/web-domain-service)

`producer` : [domain-crawler](code_sources/sb3x-project/web-domain-service/domain-crawler) ➡️ from the
domainsdb
website, it'll gather the domains that are available.

`processor (consumer & producer = processor)` : [domain-processor](code_sources/sb3x-project/web-domain-service/domain-processor)
➡️ will filter out the domains that are active and will send it to the topic.

`consumer` : [domain-service](code_sources/sb3x-project/web-domain-service/domain-service) ➡️
will consume the processed
data ([Domain](code_sources/sb3x-project/web-domain-service/domain-crawler/src/main/java/com/techprimers/domaincrawler/domain/Domain.java)),
and simply log it.

<br/>

> 💡 From this project; we can see that there is no code for configuring _kafka_. So now it is not
> tightly coupled and even if we wanted to change our messaging system, we just need
> to [change the binding dependency](./code_sources/spring-boot-2x/spring-cloud-stream-producer/pom.xml) for that
> message
> broker.
