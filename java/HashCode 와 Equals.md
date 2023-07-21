# Object

### 배경 지식 (java.lang)
java.lang 패키지는 자바 프로그래밍을 할 때 가장 기본이 되는 클래스들을 포함하고 있다. 그렇게 때문에 별도로 import 문 없이 사용할 수 있다.

예를 들어 String.class, System.class 등을 사용할 때 import 문 없이 사용하는 것도 이들 클래스들이 java.lang 패키지에서 제공하는 
클래스들이기 때문이다. 

Object 도 java.lang 패키지에 포함되어 있는 클래스로써 모든 클래스의 최고 조상이다. 따라서 해당 클래스에서 제공하는 메서드는 모든
클래스가 사용이 가능하다.


### 제공 메서드

| 메서드                              | 설명                                                                     |
|----------------------------------|------------------------------------------------------------------------|
| protected Object clone()         | 객체 자신의 복사본을 반환                                                         |
| public boolean equals(Object obj) | 객체 자신과 객체 obj가 같은 객체인지 판별                                              |
| protected void finalize()        | 객체가 소멸될 때 가비지 컬렉터에 의해 자동적으로 호출된다. 이때 수행되어야할 로직이 있으면 이걸 오버라이드해서 사용하면 된다. |
| public Class getClass()          | 객체 자신의 클래스 정보를 담고 있는 Class 인스턴스를 반환한다.                                 |
| public int hashCode()            | 객체 자신의 해시코드를 반환한다.                                                     |
| public String toString()         | 자신의 정보를 문자열로 반환한다. 오버라이딩 해서 사용하기도 한다.                                  |
| public void notify()             | 자신을 사용하려고 기다리는 쓰레드를 하나만 깨운다.                                           |
| public void notifyAll()          | 자신을 사용하려고 기다리는 모든 쓰레드를 깨운다.                                            |
| public void wait()               | 다른 쓰레드가 호출 메서드를 호출할 때까지 현재 쓰레드를 무한히 또는 지정된 시간만큼 기다리게 한다.               |


## 메서드 별로 자세히 알아보기

### equals(Object obj)

매개변수로 객체의 참조변수를 받아서 비교하여 그 결과를 boolean 값으로 알려주는 역할을 한다.

실제 코드는 다음과 같이 구현되어 있다.
```java
class Object{
    public boolean equals(Object obj){
        return (this == obj);
    }
}
```

참조변수를 매개변수로 전달받아 == 연산자로 동일 객체를 참조하고 있는지의 여부를 판단하여 반환한다. 당연한 이야기겠지만 인스턴스 주소가 같아야
(같은 주소를 참조하고 있어야지)true 를 반환한다.

본래 기능은 위와 같고(같은 인스턴스 주소를 가리키는지 비교) 오버라이드(재정의)해서 주소가 아닌, 클래스의 멤버변수라던지 등을 비교하여 판별하도록
할 수도 있다. String 클래스 뿐만 아니라 Date, File, Wrapper 클래스들은 equals() 를 오버라이딩하여 주소값이 아니라 내용을 비교하도록 되어 있다.

예시)
```java
class Ex{
    public static void main(String[] args){
        String s1 = "1";
        String s2 = "1";
        if(s1.equals(s2)) System.out.println("같다"); // 출력 !
        else System.out.println("다르다");
    }
}
```

### hashCode()

해싱기법에 사용되는 해시 함수를 구현한 것이다.
> 해싱은 데이터관리기법중 하나로 다량의 데이터를 저장하고 검색하는데 유용하다.

해시함수는 찾고자하는 값을 입력하면 그 값이 저장된 위치를 알려주는 해시코드를 반환한다. 64 bit JVM 에서는 8 byte 주소값으로 해시코드를 
만들기 때문에 해시코드가 중복될 수 있다.

앞서 살펴본 것과 같이 클래스의 인턴스변수 값으로 객체의 같고 다름을 판단하는 경우라면 hashCode도 적절히 오버라이딩해서 사용해야한다. 같은
객체라면 hashCode 메서드를 호출했을 떄의 결과값인 해시코드도 같아야하기 때문이다. 

> 해싱 기법을 사용하는 HashMap, HashSet 과 같은 클래스에 저장할 객체라면 반드시 오버라이딩해서 사용해야한다.  

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/ff72029a-f6a7-4dfd-8477-f286c89ae5de)

조금 벗어난 이야기 일수도 있지만 해싱에서 사용하는 자료구조는 다음과 같이 배열과 링크드 리스트의 조합으로 되어 있다.
저장할 데이터의 Key 를 해시함수에 넣으면 배열의 한 요소를 얻게 되고, 다시 그 곳에 연결되어 있는 LinkedList 에 저장하게 된다.
즉, 어느정도 분류해서 배열을 정해두고 인덱싱 처럼 사용하여 빠르게 값을 찾을 수 있도록 하는 것이다. 

1. 검색하고자 하는 값의 Key 로 해시함수를 호출하고
2. 반환값(HashCode)에 맞춰 배열을 찾는다.
3. 해당 값이 저장되어 있는 LinkedList를 찾는다.
4. LinkedList 에서 검색한 키와 일치하는 데이터를 찾는다.

이렇게 순서가 이루진다. LinkedList 의 경우 검색에 불리한 자료구조 이기 때문에 LinkedList 의 크기가 커질 수록 검색 속도또한 떨어진다. 반면
배열의 경우 크기가 커져도, 위치만 알면 O(1) 로 찾으면 돼서 빠르게 원하는 값을 찾을 수 있다.

예를 들어보자면 윤아, 윤이, 윤우, 윤기를 해시를 사용한 컬렉션 프레임워크에 저장할 때 HashCode 를 재정의하지 않으면 인스턴스 주소값을 기준으로
버킷(배열)에 저장되기 때문에 모두 다른 위치에 분류되어 저장이 될 것 이다. 하지만 HashCode 를 재정의해서.. 예를 들어 앞자리로 구분하도록 HashCode를
오버라이딩한다면 같은 버킷에 저장이 되겠다. 이렇게 되면 빠른 분류가 가능하겠지만 그 만큼 중복이 많아지기 때문에 LinkedList 를 사용하는 부분에서 
성능 저하가 나타나게 될 것이다.

### 배열 인덱싱의 느낌
따라서 큰 배열에 하나의 링크드 리스트만 저장되어 있는 형태가 더 빠른 속도를 낼 수 있는 것이고 링크드 리스트에 최소한의 데이터만 저장되어야 빠르다.

그렇기 때문에 해싱을 구현하는 과정에서 제일 중요한 것은 해시함수 알고리즘이며 최대한 중복된 HashCode 값을 반환하지 않도록 해시함수를 구현해야 한다.

HashMap 과 같이 해싱을 구현한 클래스에서는 Object 클래스의 HashCode 를 해시 함수로 사용하기 때문에 모든 객체에 대한 해시 함수 반환값이 다르다. 그래서 빠른 것이고..

String 클래스의 경우 HashCode 를 그대로 갖다 쓰는게 아니라 오버라이딩해서 사용하기 때문에 다른 주소값을 참조하는 인스턴스라도 같은 내용을
가진 것들이라면 HashCode 값이 일치한다.

HashSet 클래스의 경우 hashCode & equals 반환값 모두 일치해야 같은 객체로 인식한다.

```java
public class EqualsAndHashCode {

    private static Map<Info, Long> hm;
    private static Set<Info> hs;
    public static void main(String[] args) {
        hm = new HashMap<>();
        Info info1 = Info.of("윤아");
        Info info2 = Info.of("윤기");

        hm.put(info1, 3L);
        hm.put(info2, 4L);

        System.out.println(hm.size());

        hs = new HashSet<>();

        boolean result = info1.equals(info2);
        System.out.println("info1와 info2는 같은가요? : "+result);

        hs.add(info1);
        hs.add(info2);

        System.out.println(hs.size());
    }

    static class Info{
        String name;

        private Info(String name){
            this.name = name;
        }

        static Info of(String name){
            return new Info(name);
        }

        @Override
        public boolean equals(Object obj) {
            Info info = (Info) obj;
//            return Objects.equals(info.name, this.name);
            return Objects.equals(info.name.substring(0,1), this.name.substring(0,1));
        }

        @Override
        public int hashCode() {
//            return Objects.hash(name);
            return name.length();
        }
    }
}

```
#### 결과
![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/5859684a-bdec-43f6-b258-6d70581fd522)

직접 작성한 코드를 통해 알아봤다. 

1. hashCode 가 다르면 애초에 다른 객체로 인식한다.
2. hashCode 가 같더라도 equals 가 다르면 다른 객체로 인식한다.

---

> 정리하자면 새로운 클래스를 정의할 때 equals() 를 재정의 해야한다면, hashCode() 도 재정의해야한다.

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/618fd6ba-5421-4c84-a13a-d6c6f8a3bfaf)

즉 컬랙션 프레임워크 중 hash 값을 사용하는 클래스들은 위와 같은 과정을 거치며 사용되기 때문에 기대하는 결과를 얻기 위해서는 
equals() 뿐만아니라 hashcode() 도 재정의해야한다.

무조건 재정의 해주어야하는가에 대해서 생각해보자면 언제 어느 컬렉션프레임워크를 사용할지 모르는 상황에서 확장성을 위해 재정의해주는게 좀 더
안전한 방법이지 않을까 싶다.

## 참고

- https://tecoble.techcourse.co.kr/post/2020-07-29-equals-and-hashCode/
- 자바의 정석
- https://www.baeldung.com/java-hashcode