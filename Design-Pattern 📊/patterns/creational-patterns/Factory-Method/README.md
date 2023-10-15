<!-- TOC -->
* [🏭 Factory Method](#-factory-method)
  * [Applicability](#applicability)
  * [General Structure](#general-structure)
  * [Example 1](#example-1)
  * [Example 2](#example-2)
    * [Problem](#problem)
    * [Solution](#solution)
  * [Advantages](#advantages)
<!-- TOC -->

# 🏭 Factory Method

**Factory Method** is a creational design pattern that provides an interface for creating objects in a superclass, but
allows subclasses to alter the type of objects that will be created.

## Applicability

we use the Factory Method pattern when :

- Class can't anticipate the class of objects it must create.
- Class wants it's subclass to specify the objects it creates.
- Classes delegate responsibility to one of several helper subclasses, and we want to localize the knowledge of which
  helper subclass is the delegate.
- > 💡➡️ In simpler words: When there is superclass (parent class) and multiple subclasses (child classes), and we want
  to
  create the object of subclasses (child class) based on input and requirement Then we create factory class
  _**which takes the responsibility Of creating Object of class based on input.**_

<br/>

> 💡 Note : By superclass; we mean either abstract class or an interface.
>
> 💡 Note : By subclass; we mean the classes that either implement or extend the superclasses.

## General Structure

<p align="center">
  <img src="../../images/factory-method.png" width="700" />
</p>

- The creator class can contain all the methods used to
  handle the products but the "factoryMethod()" method remains abstract.

- The concrete creator classes implement the "factoryMethod()" method which
  instantiates and returns the concrete products. Every concrete creator can
  therefore create products for which he is responsible.

- Finally all the products implement the same interface so that the
  classes using the products (such as the creator) can refer to them without
  know the concrete types.

## Example 1

Consider the case of a hiring manager. It is impossible for one person to interview for each of the positions. Based on
the job opening, she has to decide and delegate the interview steps to different people.

<p align="center">
  <img src="../../images/factory-method-example.png" width="700" />
</p>

First of all we have an `Interviewer` interface and some implementations for it,then we created abstract
class `HiringManager` , Now any child can extend it and provide the required interviewer `DevelopementManager`
and `MarketingManager`.
And then it can be used as :

The code source : [source folder](./src)

```Java
public static void main(String[]args){

    DevelopmentManager developmentManager=new DevelopmentManager();
    MarketingManager marketingManager=new MarketingManager();

    developmentManager.takeInterview();
    marketingManager.takeInterview();

}
```

Output :

```
    Asking about design patterns!
    Asking about community building!
```

## Example 2

Let's say you have a few `Employee's`. They can be android, java, web, etc ... developers.
We want to create the object, based on input and not make ourselves tightly coupled;
we'll give that responsibility to a new class called `EmployeeFactory`.

### Problem

Let's firstly demonstrate the tight coupling way of creating the objects.

The code source : [employee; superclass](./src2/Employee.java)
&emsp;[web dev; child class](./src2/WebDeveloper.java)
&emsp;[android dev; child class](./src2/AndroidDeveloper.java)

```java
public static void main(String[]args){

    Employee employee=new AndroidDeveloper();
    System.out.println(employee.salary());

    Employee employee=new WebDeveloper();
    System.out.println(employee.salary());

}
```

Output :

```
    Iam an Android Developer! (nah I'm not, I'm a java developer ;))
    Web development is a bit boring for me y'know :(
```

> 💡 As you can see, we're being tightly coupled by specifying the type of the subclass.

### Solution

The SAME code source : [employee; superclass](./src2/Employee.java)
&emsp;[web dev; child class](./src2/WebDeveloper.java)
&emsp;[android dev; child class](./src2/AndroidDeveloper.java)

```java
public static void main(String[]args){

    Employee employee=EmployeeFactory.getEmployee("Web Developer");
    System.out.println(employee.salary());

    Employee employee=EmployeeFactory.getEmployee("ANDROID Developer");
    System.out.println(employee.salary());

}
```

Output :

```
    Iam an Android Developer! (nah I'm not, I'm a java developer ;))
    Web development is a bit boring for me y'know :(
```

> 💡 Note that the output is not different, but we're giving it more flexibility as we
> can add more employees like Java developer etc ...

## Advantages

1. Focus on creating object for `interface` rather than `implementation`.

   Like we are creating object for the `Employee` interface and not the web, android developer classes.
2. Loose coupling and more robust code. (we can change the type of creating a class in the runtime.)
