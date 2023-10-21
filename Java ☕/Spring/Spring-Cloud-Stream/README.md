# Spring Cloud Stream

<!-- TOC -->
* [Spring Cloud Stream](#spring-cloud-stream)
  * [Official Reference](#official-reference)
  * [What is Spring Cloud Stream?](#what-is-spring-cloud-stream)
  * [Use-case](#use-case)
  * [The Code Source](#the-code-source)
    * [Spring Boot 2.7.x](#spring-boot-27x)
    * [Spring Boot 3.x](#spring-boot-3x)
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

## The Code Source

The dependencies that we used :

- Spring web
- Spring apache kafka
- Spring cloud stream
- Lombok

### Spring Boot 2.7.x

The code source : [producer-cloud-stream](code_sources/spring-boot-2x/spring-cloud-stream-producer)

The code source : [consumer-cloud-stream](code_sources/spring-boot-2x/spring-cloud-stream-consumer)

### Spring Boot 3.x

The code source : [---]()

<br/>

> 💡 From this project; we can see that there is no code for configuring _kafka_. So now it is not
> tightly coupled and even if we wanted to change our messaging system, we just need
> to [change the binding dependency](./code_sources/spring-boot-2x/spring-cloud-stream-producer/pom.xml) for that message
> broker.

<p style="margin-top: 200px">delete this line!!!</p>
