<!-- TOC -->

* [❄️ Singleton](#-singleton)
    * [Applicability](#applicability)
    * [General Structure](#general-structure)
    * [Example](#example)
    * [Ways Of Breaking Singleton Pattern](#ways-of-breaking-singleton-pattern)
        * [Reflection API](#reflection-api)
            * [Problem - Reflection](#problem---reflection)
            * [Solution - Reflection](#solution---reflection)
        * [Deserializing The Singleton Object](#deserializing-the-singleton-object)
            * [Problem - Deserializing](#problem---deserializing)
            * [Solution - Deserializing](#solution---deserializing)
        * [Cloning The Singleton Object](#cloning-the-singleton-object)
            * [Problem - Cloning](#problem---cloning)
            * [Solution - Cloning](#solution---cloning)

<!-- TOC -->

> > > ➡️ Before everything, I recommend reading this file in order and not going through the source codes. Just to be
> > > able to understand what's going on👌😊.

# ❄️ Singleton

<b>Singleton</b> is a creational design pattern that lets you ensure that a class has only one instance, while providing
a global access point to this instance.

## Applicability

Use the Singleton pattern when

- There must be exactly one instance of a class, and it must be accessible to
  clients from a well-known access point.
- When the sole instance should be extensible by subclassing, and clients
  should be able to use an extended instance without modifying their code.

## General Structure

<p align="center">
  <img src="../../images/singleton.png" width="700" />
</p>

The Singleton class declares the static method getInstance that returns the same instance of its own class.

The Singleton’s constructor should be hidden from the client code. Calling the getInstance method should be the only way
of getting the Singleton object.

## Example

There can only be one president of a country at a time. The same president has to be brought to action, whenever duty
calls. President here is singleton.

<p align="center">
  <img src="../../images/singleton-example.png" width="700" />
</p>

Note : To create a singleton,we have to make the constructor private, override cloning (or disable), disable extension,
override serializing (or disable) and create a static variable to house the instance.

The code source : [source folder](./src/President.java)

```Java
public static void main(String[]args){
        // we can't create an instance manually since we made the constructor private
//  President president = new President();

        President presidentOne=President.getInstance();
        President presidentTwo=President.getInstance();

        System.out.println("PresidentOne hashCode:- "+presidentOne.hashCode());
        System.out.println("PresidentTwo hashCode:- "+PresidentTwo.hashCode());
        }
```

Output :

```
    PresidentOne hashCode:- 1550089733
    PresidentTwo hashCode:- 1550089733
```

> 💡 The hash value are the same!

## Ways Of Breaking Singleton Pattern

We can break this pattern in total of three ways :

### Reflection API

#### Problem - Reflection

By using `Reflection` API, we can access the constructor and create a new instance; which breaks the whole principal
of having one object. Below is a simple demonstration on how it breaks our pattern :

The code source : [reflection problem](./src2/CocaCola.java)

```java
public static void main(String[]args)throws Exception{
    CocaCola colaInstance1=CocaCola.getColaInstance();
    System.out.println("CocaColaInstance1:- "+colaInstance1.hashCode());

    Constructor<CocaCola> colaConstructor=CocaCola.class.getDeclaredConstructor();
    colaConstructor.setAccessible(true); // allows you to access private methods
    CocaCola newdInstance=colaConstructor.newInstance();

    System.out.println("CocaColaInstance2:- "+newdInstance.hashCode());
}
```

Output :

```
    CocaColaInstance1:- 500977346
    CocaColaInstance2:- 396873410
```

> 💡 Different hash values meaning new object instance created!

#### Solution - Reflection

There are two ways of solving this kind of problem:

1. Throw an exception from the constructor.

   Simply check if we already have an object of CocaCola or not (`cocaColaInstance` is not null),
   and throw an exception :

   The code source : [Solving Reflection API - constructor](./src2/CocaColaReflectionBreakingConstructor.java)
    ```java
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        CocaCola colaInstance = CocaCola.getColaInstance();
        System.out.println("CocaColaInstance1:- " + colaInstance.hashCode());
    
        Constructor<CocaCola> colaConstructor = CocaCola.class.getDeclaredConstructor();
        colaConstructor.setAccessible(true); // will be able to access private methods
        CocaCola newInstance = colaConstructor.newInstance();
    
        System.out.println("CocaColaInstance2:- " + newInstance.hashCode());
    }
    ```

   Output :
    ```
          CocaColaInstance1:- 500977346
          
          Exception in thread "main" java.lang.reflect.InvocationTargetException
              at java.base/jdk.internal.reflect.DirectConstructorHandleAccessor.newInstance(DirectConstructorHandleAccessor.java:79)
              at java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:499)
              at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:483)
              at dp.I_signleton.Singleton.main(Singleton.java:24)
          Caused by: java.lang.RuntimeException: 😝 You cannot break singleton pattern by using `Reflection` API!
              at dp.I_signleton.CocaCola.<init>(Singleton.java:35)
              at java.base/jdk.internal.reflect.DirectConstructorHandleAccessor.newInstance(DirectConstructorHandleAccessor.java:67)
              ... 3 more
    ```

   > 💡 We prevented from breaking singleton pattern!
2. Using Enum.

   Make the singleton class an enum. By doing so, you will not need any constructor or an instance field.

   The code source : [Solving Reflection API - enum](./src2/CocaColaReflectionBreakingEnum.java)

    ```java
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    
        CocaCola colaInstance = CocaCola.INSTANCE;
        System.out.println("CocaColaInstance1:- " + colaInstance.hashCode());
        colaInstance.test(); 
   
        System.out.println("You will get an exception and will not be able to create a new instance 😝👇");
        Constructor<CocaCola> colaConstructor = CocaCola.class.getDeclaredConstructor();
        colaConstructor.setAccessible(true); // will be able to access private methods
        CocaCola newInstance = colaConstructor.newInstance();
    
        System.out.println("CocaColaInstance2:- " + newInstance.hashCode());
    }
    ```

   Output :
    ```
        CocaColaInstance1:- 500977346
        I am testing whether singleton for first instance works or not!
        
        You will get an exception and will not be able to create a new instance 😝👇
        Exception in thread "main" java.lang.NoSuchMethodException: dp.I_signleton.CocaCola.<init>()
            at java.base/java.lang.Class.getConstructor0(Class.java:3617)
            at java.base/java.lang.Class.getDeclaredConstructor(Class.java:2786)
            at dp.I_signleton.Singleton.main(Singleton.java:33)
    ```

   > 💡 Pay attention to the exception : `NoSuchMethodException` which means we don't have any constructor method.

### Deserializing The Singleton Object

#### Problem - Deserializing

When an object is serialized, if we try to deserialize it we will cause the singleton pattern to break and will create a
new object.
By serializing and deserializing, I mean like writing and reading into the file (one example). Below is a simple
demonstration on how it breaks our pattern :

The code source : [deserialization problem](./src2/CocaCola.java)

```java
public static void main(String[]args){
//    serializing the object
    CocaCola colaInstance1=CocaCola.getColaInstance();
    ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream("abc.ob"));
    oos.writeObject(colaInstance1);
    System.out.println("CocaCola Instance1:- "+colaInstance1.hashCode());

    System.out.println("colaInstance serialized.");
    System.out.println("will deserialize the object...");

//    deserialize the object
    ObjectInputStream ois=new ObjectInputStream(new FileInputStream("abc.ob"));
    CocaCola colaInstance2=(CocaCola)ois.readObject();
    System.out.println("CocaCola Instance2:- "+colaInstance2.hashCode());
}
```

Output :

```
    CocaCola Instance1:- 1766822961
    
    colaInstance serialized.
    will deserialize the object...
    
    CocaCola Instance2:- 231685785
```

> 💡 The hashcode are different meaning a new instance has been created = singleton pattern broke.

#### Solution - Deserializing

We need to implement a method: `readResolve()` from the `Serializable` interface. This means we need to implement
the `Serializable` interface.
This method gets called everytime
deserialize an object.

The code source : [Solving Deserializing Object](./src2/CocaColaDeserialization.java)

Now if we run the same main method:

```java
public static void main(String[]args){
//    serializing the object
    CocaCola colaInstance1=CocaCola.getColaInstance();
    ObjectOutputStream oos=new ObjectOutputStream(new FileOutputStream("abc.ob"));
    oos.writeObject(colaInstance1);
    System.out.println("CocaCola Instance1:- "+colaInstance1.hashCode());

    System.out.println("colaInstance serialized.");
    System.out.println("will deserialize the object...");

//    deserialize the object
    ObjectInputStream ois=new ObjectInputStream(new FileInputStream("abc.ob"));
    CocaCola colaInstance2=(CocaCola)ois.readObject();
    System.out.println("CocaCola Instance2:- "+colaInstance2.hashCode());
}
```

Output :

```
    CocaCola Instance1:- 1766822961
    
    colaInstance serialized.
    will deserialize the object...
    
    CocaCola Instance2:- 1766822961
```

> 💡 Hashcode are same! singleton pattern didn't break :)

### Cloning The Singleton Object

#### Problem - Cloning

Cloning will also do the same as deserializing. It will clone the object by creating a new instance of that same object.
Below is a simple demonstration on how it breaks our pattern :

The code source : [cloning problem](./src2/CocaColaCloningProblem.java)

```java
public static void main(String[]args){
    CocaCola colaInstance1=CocaCola.getColaInstance();
    System.out.println("CocaCola Instance1:- "+colaInstance1.hashCode());

    CocaCola clonedColaInstance=(CocaCola)colaInstance1.clone();
    System.out.println("CocaCola Instance2:- "+clonedColaInstance.hashCode());
}
```

Output :

```
    CocaCola Instance1:- 500977346
    CocaCola Instance2:- 396873410
```

> 💡 Hashcode are different meaning we broke the singleton pattern!

#### Solution - Cloning

We need to override a method: `clone()` from the `Cloneable` interface. This means we need to implement the `Cloneable`
interface.

The code source : [Solving Object Cloning](./src2/CocaColaCloning.java)

Now if we run the same main method :

```java
public static void main(String[]args){
    CocaCola colaInstance1=CocaCola.getColaInstance();
    System.out.println("CocaCola Instance1:- "+colaInstance1.hashCode());

    CocaCola clonedColaInstance=(CocaCola)colaInstance1.clone();
    System.out.println("CocaCola Instance2:- "+clonedColaInstance.hashCode());
}
```

Output :

```
    CocaCola Instance1:- 500977346
    CocaCola Instance2:- 500977346
```

> 💡 The hashcode is same! singleton didn't break!