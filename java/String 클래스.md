# String 클래스

## String 클래스란?

java.lang 패키지(자바 프로그래밍에 가장 기본이 되는 클래스들을 모아놓은 package)에서 제공하는 기본 클래스로
문자열을 위한 클래스이다.

문자열을 저장하고 이를 다루는데 필요한 메서드를 함께 제공하고 있다.

## 변경 불가능한(Immutable) 클래스

String 클래스는 문자열을 저장하기 위해서 문자열 배열 참조변수를 멤버변수(인스턴스 변수)로 정의해놓고 있으며
인스턴스 초기화(생성) 시 생성자의 매개변수로 받는 문자열로 인스턴스 변수를 초기화한다.

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/278b1964-df26-4231-a879-62d7b00fefcb)

이 인스턴스 변수를 살펴보면 다음과 같이 final 로 선언되어 있으며 보다 싶이 String 클래스 자체가 final 로 선언되어 있기 때문에
상속이 불가능하다.

생성된 String 클래스의 인스턴스는 한번 생성되는 시점에 그 값을 읽을 수만 있고, 변경할 수는 없는 불변 객체인데
이러한 특성으로 인해 연산을 할 때 불필요한 메모리공간의 낭비가 발생할 수 있다.

## 연산자를 활용할 때 메모리

```java
class Ex{
    String a = "a";
    String b = "b";
    public static void main(String[] args) {
        // 새로운 메모리공간에 "ab"이 담긴 String 인스턴스가 생성 
        a = a + b;
        
        // 계속 새로운 String 인스턴스 생성... 메모리 공간 낭비
        for(int i = 0; i<100000; i++){
            a += b;
        }
    }
}
```

다음과 같은 `+` 연산을 하게 되면 문자열이 결합되게 되는데, 아까 말했듯이 String 인스턴스는 불변 객체로
한번 생성된 문자열은 변경할 수 없다. 즉, `+` 연산을 하면 인스턴스 내부의 멤버 변수 value 가 변경되는게 아니라
새로운 인스턴스가 생성되어 새로운 메모리공간을 차지하게 된다.

## 문자열의 비교

문자열을 만들 때는

1. 문자열 리터럴을 지정하는 방법
2. String 클래스의 생성자를 사용해서 만드는 방법

이렇게 2 가지 방법이 있다.

```java
class Ex{
    public static void main(String[] args) {
        String str1 = "a"; // 1
        String str2 = "a"; // 2
        
        String str3 = new String("a"); // 3
        String str4 = new String("a"); // 4
    }
}
```

리터럴의 경우 이미 존재하는 것을 재사용하는 것으로 참조변수 str1, str2 는 모두 같은 주소를 참조하지만
new 키워드를 통해 인스턴스를 생성하는 방식인 str3, str4 는 각각 새로운 메모리 공간을 할당받아 생성된다.

예제를 통해 동등성과 동일성을 비교해보자

```java
public class StringEx {
    public static void main(String[] args) {
        String str1 = "abc";
        String str2 = "abc";
        String str3 = new String("abc");
        String str4 = new String("abc");

        System.out.println("str1 과 str2 는 동등한가 ? " + str1.equals(str2));
        System.out.println("str3 과 str4 는 동등한가 ? " + str3.equals(str4));

        System.out.println("str1 과 str2 는 동일한가 ? " + ( str1 == str2 ));
        System.out.println("str3 과 str4 는 동일한가 ? " + ( str3 == str4 ));
    }
}

```

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/aa43c9a8-a975-453f-af95-388415082117)

리터럴을 통해 초기화한 값은 동일하다. 반면 new 키워드를 통해 생성한 str3, str4 는 동등하지만 각각 다른 메모리 공간을 할당받아 생성되기 때문에
동일하진 않다.

#### String 의 equals() 살펴보기
![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/21de642b-2171-4024-a107-d507fedce381)

> String 클래스의 경우 Object.equals() 를 오버라이딩하여 동등성을 비교한다. 아래 정의되어 있는 메서드와 같이 애초에 동일하면 동등하다 판단하고
> true 를 반환하고, 그 외의 경우 타입, coder, value 등을 비교하여 동등성을 판단한다.

참고로 동일하다는 것은 두 인스턴스가 완전히 같다는 것(같은 메모리 공간에 위치한 동일한 존재)을 의미하고, 동등하다는 것은 equals() 와 같이
두 인스턴스가 서로 같은 정보를 가지고 있는 것을 의미한다.

## 문자열 리터럴

프로그래밍에서 상수는 `final` 키워드를 붙여 선언하여 값을 한번 저장하면 변경할 수 없는 변수를 말한다. 
리터럴은 변수의 값이 변하지 않는 데이터를 의미한다. 12, 123, 3.13, 'A', "123" 등을 의미하며

`final int yaer = 2013` 중 year 는 상수, 2013은 리터럴이다. 그 자체로의 값을 의미하는 것이다.

리터럴에는 논리, 정수, 실수, 문자, 문자열이 있다.

문자열 리터럴의 경우 컴파일 시에 클래스 파일에 한번만 저장되어 실행중 계속 쓰인다. 즉, 동일 주소값을 참조하여 사용하므로
따라서 new 키워드를 통해 생성하여 사용하는 것보다 상황에 따라서 적절하게 문자열 리터럴을 활용하는것이 훨씬 효율적이다. 

## 참고

자바의 정석