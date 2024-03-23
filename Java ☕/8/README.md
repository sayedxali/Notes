# Java 8

## Lambda expressions and Functional interfaces

**@FunctionalInterface**

- in a `@FunctionalInterface` we only one method (abstract method). We can have default methods as well but only one
  method.
    ```java
    @FunctionalInterface
    public interface MyFunctionalInterface {
        
        void method1();
        
        default void method1() {
            System.out.println("Default method 1");
        }
    
        default void method2() {
            System.out.println("Default method 2");
        }
    
        default void method3() {
            System.out.println("Default method 3");
        }
        
    }
    ```
- for that method that is in the functional interface, we can write lambda expression.
  ```java
  public static void main(String[] args) {
    MyFunctionalInterface functionalInterface = () -> System.out.println("lambda implementation");
    functionalInterface.method1();
  }
  ```
  > 💡 in traditional approach, we'd make a class and then implement that interface and then provide this implementation.
  In this approach we reduced the code length.