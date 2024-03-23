<!-- TOC -->

* [Connecting a springboot application to a `mongodb docker image`](#connecting-a-springboot-application-to-a-mongodb-docker-image)
    * [Method #1 : Manual connection through `cmd`](#method-1--manual-connection-through-cmd)
    * [Method #2 : Connecting using `docker-compose.yml` file](#method-2--connecting-using-docker-composeyml-file)

<!-- TOC -->

# Connecting a springboot application to a `mongodb docker image`

There are 2 ways of connecting:

1. Using `--link` and manually connecting through `cmd`.
2. Using `docker-compose` and running it effortlessly.

We'll demonstrate in a very simple springboot application, so the dependencies are:

```xml

<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-mongodb</artifactId>
    </dependency><!--mongodb starter-->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency><!--web starter-->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency><!--lombok-->
</dependencies>
```

Build the springboot application : [the source for application](./src/main)

## Method #1 : Manual connection through `cmd`

- **Steps 1 :**

  Build the springboot application : [the source for application](./src/main)

  > 💡don't look at [Dockerfile](./Dockerfile), [docker-compose.yml](./docker-compose.yml)
  > file & [application.yml](./src/main/resources/application.yaml) file yet, we'll go through it step by step.

- **Step 2 :**

  Run the mongodb docker image: `docker run -p 27017:27107 --name mongo-container mongo:7.0`.

  Run `docker ps` and you should see your mongo container in running state.

  > 💡 Remember the container name of the mongo image |=> `mongo-container` :: we'll use that as the **host** of the
  database
  in configuring **datasource** of our application.

- **Step 3 :**

  Configure the datasource in the application.yml file. [source](./src/main/resources/application.yaml)
  ```yaml
  spring:
  data:
    mongodb:
      database: users
      host: mongo-container # *** 
      port: 27017
  ```

  > 💡*** : the host name is the name that we asked you to remember => it is the name of the container that we provided.

- **Step 4 :**

  Dockerize your springboot application : [source](./Dockerfile).
  ```dockerfile
  FROM openjdk:22-slim-bullseye
  LABEL authors="Duke-Of-Java"
  
  WORKDIR /app
  
  COPY target/*.jar /app/app.jar
  
  EXPOSE 8080
  
  ENTRYPOINT ["java", "-jar", "app.jar"]
  ```

  Now open up a `cmd` in the same directory of your application and dockerfile;
  run `docker build -t springboot-mongodb:1.0 .`.

  Your image of your application is ready.

- **Step 5 :**

  Run and connect your springboot application's image with mongodb container :

  `docker run -d -p 8080:8080 --name springboot-mongodb --link mongo-container:mongo springboot-mongodb:1.0`

  ✅ Done. You are now connected to mongodb image.

## Method #2 : Connecting using `docker-compose.yml` file

- **Step 1 :**

  Create `docker-compose.yml` file. [the source](./docker-compose.yml)
  ```dockerfile
  version: "3"
  services:
    mongodb:
      image: mongo:7.0
      container_name: "mongodb"
      ports:
      - "27017:27017"
    
    springboot-mongodb-app:
      image: springboot-mongodb:1.0
      container_name: "springboot-mongodb-app"
      ports:
      - "8080:8080"
      depends_on:
        - mongodb
  ```

- **Step 2 :**

  Accordingly, the [application.yml](./src/main/resources/application.yaml) file will also change based on
  docker-compose file.
  ```yaml
  spring:
  data:
    mongodb:
      database: users
      host: mongodb # ***
      port: 27017
  ```

  > 💡*** : the name is same as the name in the docker-compose, the mongo part.
  