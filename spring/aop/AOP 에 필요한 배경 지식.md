# 1. ThreadLocal

## 쓰레드 로컬이란?

쓰레드 로컬은 해당 쓰레드만 접근할 수 있는 특별한 저장소를 말한다.

쉽게 이야기해서 물건 보관 창구를 떠올리면 된다.
여러 사람이 같은 물건 보관 창구를 사용하더라도 창구 직원은 사용자를 인식해서 사용자별로
확실하게 물건을 구분해준다.

### 일반적인 변수 필드
일반적인 변수 필드의 경우 여러 쓰레드가 같은 인스턴스의 필드에 접근하면 처음 쓰레드가 보관한 데이터가 사라질 수 있다.
즉, 동시성 문제가 발생할 수 있다.

<img width="807" alt="image" src="https://user-images.githubusercontent.com/53935439/209423889-7df70ba8-3271-4a4b-a781-f2181ea6c1e6.png">
<img width="808" alt="image" src="https://user-images.githubusercontent.com/53935439/209423922-1f43a835-c10a-4b5d-bcf0-0d9b92f3c826.png">
<img width="802" alt="image" src="https://user-images.githubusercontent.com/53935439/209424615-04c3801f-ba01-4932-8d28-d9cd9100a732.png">

이러한 경우 Thread-A 입장에서 저장한 데이터와 조회한 데이터가 다른 문제가 발생한다. 그리고 이처럼 여러 쓰레드에서 동시에 같은 인스턴스의 필드값을 변경하면서
발생하는 문제를 **동시성 문제**라고 한다.

동시성 문제는 이처럼 여러 쓰레드가 인스턴스 필드에 접근하는 상황에서 트래픽이 점점 많아질수록 자주 발생하게 된다.

특히 스프링 빈처럼 **싱글톤 객체의 필드를 변경**하며 사용할 때에는 이러한 동시성 문제를 항상 조심해야 한다.

### 쓰레드 로컬
하지만 쓰레드 로컬의 경우 각 쓰레드마다 별도의 내부 저장소를 사용하기 때문에 같은 인스턴스에 로컬 필드에 접근하더라도 문제가 없다.

<img width="810" alt="image" src="https://user-images.githubusercontent.com/53935439/209423995-58a16440-dd56-499d-a5eb-d4c34b801853.png">

## 쓰레드 로컬 사용법

- 값 저장 : ThreadLocal.set()
- 값 조회 : ThreadLocal.get()
- 값 제거 : ThreadLocal.remove()

## 예제

이제 예시를 통해 쓰레드 로컬의 사용방법을 알아보자

```java
class ThreadLocalService{
    private ThreadLocal<String> nameStore = new ThreadLocal<>(); // ThreadLocal 사용으로 동시성 문제 해결
    public String logic(String name){
        nameStore.set(name);
        sleep(1000);
    }
    private void sleep(int millis){
        try{
            Thread.sleep(millis);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}
class Test{
    private ThreadLocalService servie = new ThreadLocalService();
    @Test
    void ThreadLocal(){
        
        Runnable userA = ()->{
          service.logic("userA");  
        };
        Runnable userB = ()->{
          service.logic("userB");
        };
        
        Thread threadA = new Thread(userA);
        threadA.setName("thread-A");
        Thread threadB = new Thread(userB);
        threadB.setName("thread-B");

        threadA.start();
        sleep(100);
        threadB.start();
        sleep(2000);
    }
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}
```

위 예제에서 ThreadA와 ThreadB가 모두 같은 인스턴스의 필드값 nameStore를 변경하는 로직을 수행하기 때문에
동시성 문제가 발생할 수 있다. 이때 해당 필드값을 ThreadLocal으로 선언하면 각각의 쓰레드마다 별도의 내부 저장소를 사용하기 때문에
동시성 문제를 해결할 수 있다.

## 주의사항

쓰레드 로컬을 사용할 때는 주의사항이 있다.

사용자의 요청이 끝날 때 쓰레드 로컬의 값을 ThreadLocal.remove() 를 통해서 꼭 제거해야 한다.
쓰레드 로컬을 사용할 때는 이 부분을 꼭! 기억해야 한다.

쓰레드 로컬의 값을 사용 후 제거하지 않고 그냥 두면 WAS(톰캣)처럼 쓰레드 풀을 사용하는 경우에 심각한 문제가 발생할 수 있다.

다음 예시를 보자

<img width="808" alt="image" src="https://user-images.githubusercontent.com/53935439/209425113-11ce6675-894b-4ac8-addd-e6db6c1222dd.png">
<img width="808" alt="image" src="https://user-images.githubusercontent.com/53935439/209425134-da7f3d1f-e733-4d4e-a372-c0f343f5e83a.png">
<img width="811" alt="image" src="https://user-images.githubusercontent.com/53935439/209425164-622bd8b7-09cf-4610-9ef0-2ae4bb6cc3ff.png">

만약 Thread-A의 쓰레드 로컬 값을 제거하지 않고
Thread-B가 데이터를 조회하게 될때 쓰레드 풀에서 thread-A 가 할당되게 된다면(물론 다른 쓰레드가 할당 될수도 있음) 사용자A값이 반환되게 되므로
결과적으로 사용자 B가 사용자 A의 데이터를 확인하게 되는 심각한 상황이 벌어질 수 있다.

그렇기 때문에 꼭 이부분을 주의해서 개발해야 한다.

# 2. 템플릿 메서드 패턴

## 정의

GOF 디자인 패턴에서는 템플릿 메서드 패턴을 다음과 같이 정의했다.

>템플릿 메서드 디자인 패턴의 목적은 다음과 같습니다.
> "작업에서 알고리즘의 골격을 정의하고 일부 단계를 하위 클래스로 연기합니다. 템플릿 메서드를 사용하면
하위 클래스가 알고리즘의 구조를 변경하지 않고도 알고리즘의 특정 단계를 재정의할 수 있습니다." [GOF]

풀어서 설명하면 다음과 같다.
부모 클래스에 알고리즘의 골격인 템플릿을 정의하고, 일부 변경되는 로직은 자식 클래스에 정의하는 것이다.
이렇게 하면 자식 클래스가 알고리즘의 전체 구조를 변경하지 않고, 특정 부분만 재정의할 수 있다.
결국 상속과 오버라이딩을 통한 다형성으로 문제를 해결하는 것이다.

## 예제

그렇다면 이런 템플릿 메서드 패턴을 어떻게 활용할 수 있을까?

코드의 전체적인 흐름을 보기 위해 로그 추적기를 만들어 적용시킨다고 생각해보자.
우리는 MVC 구조로 작성했기 때문에 Service, Controller, Repository에 모두 로그 추적 기능을 적용시켜야한다.


Controller
````java
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final LogTrace trace; // 로그 추적기
    
    @GetMapping("/v3/request")
    public String request(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderController.request()");
            orderService.orderItem(itemId); // 핵심 기능 - 변함
            trace.end(status);
            return "ok";
        } catch (Exception e) { 
            trace.exception(status, e);
            throw e; 
        } 
    }
}
````

Service
```java
@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final LogTrace trace; // 로그 추적기
    public void orderItem(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderService.orderItem()");
            orderRepository.save(itemId); // 핵심 기능 - 변함
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        } 
    }
}
```

Repository
````java
@Repository
@RequiredArgsConstructor
public class OrderRepositoryV3 {

    private final LogTrace trace;
    public void save(String itemId) {
        TraceStatus status = null;
        try {
            status = trace.begin("OrderRepository.save()"); 
            
            // 핵심 기능 - 변함
            if (itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생!");
            }
            sleep(1000);
            
            trace.end(status);
        } catch (Exception e) {
            trace.exception(status, e);
            throw e; }
    }
    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    } 
}
````

코드(Service, Repository, Controller)를 살펴보면 변하는 부분과 변하지 않는 부분을 살펴볼 수 있다.
- 변하는 부분 : 핵심 기능 (해당 객체가 제공하는 고유 기능)
- 변하지 않는 부분 : 부가 기능 (핵심 기능을 보조하기 위해 제공되는 기능)

이대로가 좋은 설계일까? 아니다.

코드를 살펴보면 부가 기능이 중복으로 똑같이 반복되고 있다. 만약 부가 기능을 수정해야 할 경우 해당 기능이 들어간
모든 코드를 일일이 찾아서 수정해야한다.

좋은 설계라면 변경이 일어났을 때 쉽게 대처할 수 있어야 한다..

우리는 이러한 상황을 해결하기 위해 모듈화를 진행해야한다. 즉 로그를 남기는 부분(부가 기능)에 SRP를 적용시켜 변경지점을 하나로 모아
변경에 쉽게 대처할 수 있는 구조로 만들어야한다.

템플릿 메서드 패턴을 사용하면 이러한 구조를 실현할 수 있다.

```java
public abstract class AbstractTemplate<T> {
    private final LogTrace trace; // 로그 추적기
    public AbstractTemplate(LogTrace trace) {

        this.trace = trace;
    }
    public T execute(String message) { // 부가 기능 : 변하지 않는 부분
        TraceStatus status = null;
        try {
            status = trace.begin(message); 
            //로직 호출
            T result = call();
            trace.end(status); 
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e; }
    }
    protected abstract T call(); // 핵심 기능 : 변하는 부분
}
```

부가 기능의 경우 변하지 않는 부분이므로 **AbstractTemplate** 클래스에 모아서 정의하고
핵심 기능의 경우 변하는 부분이므로 각 객체에 맞게 오버라이딩을 통해 재정의하도록 한다.

```java
@RestController
@RequiredArgsConstructor
public class OrderControllerV4 {
    private final OrderServiceV4 orderService;
    private final LogTrace trace;

    @GetMapping("/v4/request")
    public String request(String itemId) {
        AbstractTemplate<String> template = new AbstractTemplate<>(trace) {
            @Override
            protected String call() {
                orderService.orderItem(itemId);
                return "ok";
            }
        }; // 인스턴스 생성과 동시에 핵심 기능 알맞게 오버라이딩
        
        return template.execute("OrderController.request()"); // 모듈 실행
    }
}
```

이제 익명 클래스를 통해 객체 인스턴스를 생성함과 동시에 핵심 기능을 오버라이딩 해주고 실행한다면 똑같은 결과를 얻을 수 있다.
동시에 모듈화되었기 떄문에 부가 기능에 수정이 발생하게 된다면 이제는 AbstractTemplate 클래스만 수정하면 된다.

### SRP를 지키면
> 변경 지점을 하나로 모아 변경에 쉽게 대처할 수 있는 구조가 됨

이렇듯 좋은 설계는 변경이 일어 날때 용이하다.

# 3. 전략 패턴

## 정의

GOF 디자인 패턴에서 정의한 전략 패턴의 의도는 다음과 같다.
> 알고리즘 제품군을 정의하고 각각을 캡슐화하여 상호 교환 가능하게 만들자.
> 전략을 사용하면 알고리즘을 사용하는 클라이언트와 독립적으로 알고리즘을 변경할 수 있다.

템플릿 메서드 패턴의 경우 부모 클래스에 변하지 않는 부분(템플릿, 골격)을 정의하고, 변하는 부분을
자식 클래스에서 오버라이딩을 통해 재정의하는 방식으로 문제를 해결했다.

하지만 이런식으로 구성하게 되면 부모 클래스를 상속하여 오버라이딩하는 방식은 결국 강한 결합을 형성하게 되므로
자식 클래스에서는 부모 클래스의 기능을 사용하지 않는 경우에도 부모 클래스를 알아야한다.
결국 부모 클래스에 수정이 발생하게 되면 자식 클래스에 영향을 줄 수 있다.

전략 패턴의 경우 변하지 않는 부분을 정의하고, 변하는 부분을 인터페이스를 통해 구현하도록 하여 문제를 해결한다.


<img width="805" alt="image" src="https://user-images.githubusercontent.com/53935439/209493636-0927e593-4de7-488c-b562-117613a7ff45.png">

```java
public interface Strategy {
      void call();
}
```
```java
public class StrategyLogic1 implements Strategy {
      @Override
      public void call() {
            log.info("비즈니스 로직1 실행"); 
      }
}
```
```java
public class ContextV1 {
      private Strategy strategy; // 변하는 부분
      public ContextV1(Strategy strategy) { 
          this.strategy = strategy; 
        }
        
        public void execute() {
            long startTime = System.currentTimeMillis(); 
            //비즈니스 로직 실행
            strategy.call(); //위임
            //비즈니스 로직 종료
            long endTime = System.currentTimeMillis(); 
            long resultTime = endTime - startTime; 
            log.info("resultTime={}", resultTime);
      } 
}
```
```java
class StrategyTest {
    @Test
    void strategyV1() {
        Strategy strategyLogic1 = new StrategyLogic1();
        ContextV1 context1 = new ContextV1(strategyLogic1);
        context1.execute();
    }
}
```
어딘가 익숙한 코드라면 당연하다. 스프링의 의존관계 주입에서 사용하는 방식이 바로 전략패턴이다.

위의 코드의 경우 '선 조립 후 실행' 방법이며 실행 시점에는 이미 조립, 즉 주입이 끝났으므로 단순히 실행만하면 되지만
전략이 정해진, 즉 의존관계 주입이 이루어진 이후에는 변경하기 번거롭다는 단점이 있다.

좀 더 유연한 방법으로는 실행시점에 변하는 부분(구현체)를 파라미터로 넘겨받아 사용하는 방법이 있다.

```java
public class ContextV2 {
    public void execute(Strategy strategy) {
        long startTime = System.currentTimeMillis(); 
        //비즈니스 로직 실행
        strategy.call(); //위임
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis(); 
        long resultTime = endTime - startTime; 
        log.info("resultTime={}", resultTime);
           
    } 
}
```
```java
public class StrategyTest {

      @Test
      void strategyV1() {
          ContextV2 context = new ContextV2();
          context.execute(new StrategyLogic1());
          context.execute(new StrategyLogic2());
      } 
}
```

# 4. 템플릿 콜백 패턴

## 정의

콜백 정의
> 프로그래밍에서 콜백(callback) 또는 콜애프터 함수(call-after function)는
> 다른 코드의 인수로서 넘겨주는 실행 가능한 코드를 말한다.
> 콜백을 넘겨받는 코드는 이 콜백을 필요에 따라 즉시 실행할 수도 있고,
> 아니면 나중에 실행할 수도 있다. (위키백과 참고)

일전에 전략 패턴에서 실행 시점에 의존성을 주입받아 사용하는 방식을 설명했었다.

### 전략 패턴 - 실행 시점에 주입 : 템플릿 메서드 패턴
```java
public class ContextV2 {
    public void execute(Strategy strategy) {
        long startTime = System.currentTimeMillis(); 
        //비즈니스 로직 실행
        strategy.call(); //위임
        //비즈니스 로직 종료
        long endTime = System.currentTimeMillis(); 
        long resultTime = endTime - startTime; 
        log.info("resultTime={}", resultTime);
           
    } 
}
```
```java
public class StrategyTest {

      @Test
      void strategyV1() {
          ContextV2 context = new ContextV2();
          context.execute(new StrategyLogic1());
          context.execute(new StrategyLogic2());
      } 
}
```
이러한 패턴을 **템플릿 콜백 패턴**이라고 한다.

참고로 템플릿 콜백 패턴은 GOF 패턴은 아니고,
스프링 내부에서 이런 방식을 자주 사용하기 때문에, 스프링 안에서만 이렇게 부른다.
전략 패턴에서 템플릿과 콜백 부분이 강조된 패턴이라 생각하면 된다.

스프링에서는
- JdbcTemplate
- RestTemplate
- TransactionTemplate
- RedisTemplate

처럼 다양한 템플릿 콜백 패턴이 사용된다. 스프링에서 이름에 XxxTemplate 가 있다면
템플릿 콜백 패턴으로 만들어져 있다 생각하면 된다.

<img width="804" alt="image" src="https://user-images.githubusercontent.com/53935439/209513527-acd5dc3f-197b-492e-9f29-cf97acffab0d.png">

템플릿 콜백 패턴을 활용하여 로그 추적기능을 만들어보자

```java
public interface TraceCallback<T> {
    T call();
}

```
먼저 변하는 부분을 인터페이스로 선언하여 나중에 구현할 수 있도록 한다.

```java
public class TraceTemplate {
    private final LogTrace trace;
    public TraceTemplate(LogTrace trace) {
        this.trace = trace;
    }
    public <T> T execute(String message, TraceCallback<T> callback) {
        TraceStatus status = null;
        try {
            status = trace.begin(message);
            //로직 호출
            T result = callback.call();
            trace.end(status);
            return result;
        } catch (Exception e) {
            trace.exception(status, e);
            throw e;
        }
    }
}
```
템플릿을 만든 후 변하지 않는 로그 추적기능을 작성한다.
변하는 부분(핵심 기능)은 실행 시점에 의존성을 주입받을 수 있도록 한다.

이제 MVC에 템플릿 콜백 패턴을 적용한 로그 추적기능을 추가해보자

```java
@Controller
public class OrderControllerV5 {
    private final OrderServiceV5 orderService;
    private final TraceTemplate template;
    public OrderControllerV5(OrderServiceV5 orderService, LogTrace trace) {
        this.orderService = orderService;
        this.template = new TraceTemplate(trace);
 }
      @GetMapping("/v5/request")
      public String request(String itemId) {
          return template.execute("OrderController.request()", new TraceCallback<>() {
              @Override
              public String call() {
                  orderService.orderItem(itemId);
                  return "ok";
              }
}); }
}
```
````java
@Service
  public class OrderServiceV5 {
      private final OrderRepositoryV5 orderRepository;
      private final TraceTemplate template;
      public OrderServiceV5(OrderRepositoryV5 orderRepository, LogTrace trace) {
          this.orderRepository = orderRepository;
          this.template = new TraceTemplate(trace);
      
 }
      public void orderItem(String itemId) {
          template.execute("OrderService.request()", () -> {
              orderRepository.save(itemId);
              return null;
          });
} }
````

````java
@Repository
  public class OrderRepositoryV5 {
      private final TraceTemplate template;
      public OrderRepositoryV5(LogTrace trace) {
          this.template = new TraceTemplate(trace);
      }
      public void save(String itemId) {
          template.execute("OrderRepository.save()", () -> {
            //저장 로직
            if (itemId.equals("ex")) {
                throw new IllegalStateException("예외 발생!"); 
            }
            sleep(1000);
            return null;
          }); 
      }
      private void sleep(int millis) {
          try {
              Thread.sleep(millis);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
} }
````
위 코드에서 볼 수 있듯이 실행 시점에 익명클래스 또는 람다로 변하는 부분을 구현함과 동시에 파라미터로 던져준다.
이후 템플릿의 메서드를 통해 실행하면 원하는 결과를 얻을 수 있다.

# 5. 프록시 패턴과 데코레이션 패턴 정리

## 프록시란?

클라이언트와 서버 개념에서는 일반적으로 **직접 호출**을 통해 통신이 이루어진다.

이와 다르게 직접 요청이 아니라 대리자를 통해서 대신 간접적으로 서버에 요청, 즉 통신이 이루어질 수 있는데 이때 **대리자**를 프록시라고 한다.

<img width="801" alt="image" src="https://user-images.githubusercontent.com/53935439/209613349-20fdb69d-891f-4bff-87ba-e81bb7e61abe.png">

서버와 프록시는 같은 인터페이스를 사용해서 구현체를 만들고 클라이언트는 인터페이스에 의존하여
클라이언트 입장에서는 어떤 구현체가 올지 신경쓰지 않고 호출해야 한다.

<img width="803" alt="image" src="https://user-images.githubusercontent.com/53935439/209613959-ea5553e8-5f82-4a0a-a8ed-abd482076a55.png">

따라서 클라이언트가 호출하게 되면 프록시를 통해 어떤 일련의 작업이 진행되며 결과에 따라 서버를 호출하게 된다.
<img width="802" alt="image" src="https://user-images.githubusercontent.com/53935439/209614228-629c1c73-4f3b-44ec-a2b6-a2fe1f2135b5.png">

## 프록시의 주요 기능

프록시가 중간에서 할 수 있는 주요 기능은 크게 2가지로 분류할 수 있다.

- 접근 제어
    - 권한에 따른 접근 차단
    - 캐싱
    - 지연 로딩
- 부가 기능 추가
    - 원래 서버가 제공하는 기능에 더해서 부가 기능을 수행한다.
    - 예) 요청 값이나, 응답 값을 중간에 변형한다.
    - 예) 실행 시간을 측정해서 추가 로그를 남긴다.

즉, 프록시를 활용하면 접근 제어와 부가 기능 추가를 수행할 수 있다.

프록시 패턴과 데코레이션 패턴은 둘 다 프록시를 사용하며
GOF 디자인 패턴에서는 둘을 의도에 따라서 아래와 같이 분류하고 있다.

- 프록시 패턴 : **접근 제어**가 목적
- 데코레이션 패턴 : **새로운 기능 추가**가 목적

# 프록시 패턴

프록시 패턴은 프록시를 사용하여 접근 제어가 목적인 패턴이다.

예시를 통해 활용 방법을 알아보자

```java
public interface Subject {
    String operation();
}

@Slf4j
public class RealSubject implements Subject{
    @Override
    public String operation() {
        log.info("실제 객체 호출");
         sleep(1000);
        return "data";
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class ProxyPatternClient {

    private Subject subject;

    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    public void execute(){
        subject.operation();
    }
}

public class ProxyPatternTest {
    @Test
    void noProxyTest(){
        RealSubject realSubject = new RealSubject();
        ProxyPatternClient client = new ProxyPatternClient(realSubject);
        client.execute();
        client.execute();
        client.execute();
    }
}
```

클라이언트는 **직접 호출**을 통해 서버와 통신을 진행하고 있으며 1번 호출할 때마다 1초, 3번 호출했으니 총 3초의 소요시간이 걸린다.

똑같은 정보를 3번 조회한다고 가정했을 때, 만약 캐싱을 통해 처리할 수 있다면 훨씬 빠르게 처리할 수 있다.
프록시를 활용하여 접근 제어를 통해 캐싱 처리를 해보자

```java
@Slf4j
public class CacheProxy implements Subject{

    private Subject target; // 실제 호출할 객체
    private String cacheValue; // 캐시 데이터


    public CacheProxy(Subject target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("프록시 호출");
        if(cacheValue == null){
            cacheValue = target.operation();
        }
        return cacheValue;
    }
}
```
```java
public class ProxyPatternTest {
    @Test
    void cacheProxyTest() {
        RealSubject realSubject = new RealSubject();
        CacheProxy cacheProxy = new CacheProxy(realSubject);
        ProxyPatternClient proxyPatternClient = new ProxyPatternClient(cacheProxy);
        proxyPatternClient.execute(); // 프록시 첫 호출 -> 반환값이 프록시 캐시에 저장
        proxyPatternClient.execute(); // 캐시 데이터 반환
        proxyPatternClient.execute(); // 캐시 데이터 반환
    }
}
```

프록시 클래스는 인스턴스를 생성할 때, 타겟 클래스의 인스턴스를 주입받아
이후 클라이언트의 호출 시 캐싱 된 데이터가 없을 경우 타켓 객체를 호출하여 처리하고
캐싱 된 데이터가 있을 경우 그대로 반환하게 된다.

따라서 클라이언트 입장에서는 똑같이 3번의 요청을 보내지만 첫 번째를 제외하고 이후 데이터는 캐싱된 데이터를
반환받기 때문에 매우 빠르게 데이터를 조회할 수 있게 된다.

# 데코레이션 패턴

데코레이션 패턴은 프록시를 사용하여 새로운 기능 추가가 목적인 패턴이다.

예시를 통해 알아보자

```java
public interface Component {
    String operation();
}

@Slf4j
public class RealComponent implements Component{
    @Override
    public String operation() {
        log.info("realComponent 실행");
        return "data";
    }
}

@Slf4j
public class DecoratorPatternClient {

    private Component component;

    public DecoratorPatternClient(Component component) {
        this.component = component;
    }

    public void execute(){
        String result = component.operation();
        log.info("result = {}",result);
    }
}

@Slf4j
public class DecoratorPatternTest {
    @Test
    void noDecorator() {
        RealComponent realComponent = new RealComponent();
        DecoratorPatternClient client = new DecoratorPatternClient(realComponent);
        client.execute(); // 직접 호출
    }
}

```
위 코드의 경우 클라이언트가 직접 호출을 통해 서버와 통신을 진행하고 있다.

여기에
1. 동작시간을 출력하고
2. 로그를 꾸며주는

부가 기능을 추가할 때 데코레이션 패턴을 사용하게 되면
위의 코드를 수정하지 않고도 부가 기능을 추가할 수 있다.

```java
@Slf4j
  public class TimeDecorator implements Component {
      private Component component;
      public TimeDecorator(Component component) {
          this.component = component;
      }
      @Override
      public String operation() {
        log.info("TimeDecorator 실행");
        long startTime = System.currentTimeMillis();
        String result = component.operation();
        long endTime = System.currentTimeMillis();
        long resultTime = endTime - startTime; 
        log.info("TimeDecorator 종료 resultTime={}ms", resultTime); 
        return result;
      } 
}

@Slf4j
public class MessageDecorator implements Component {

    private Component component;

    public MessageDecorator(Component component) {
        this.component = component;
    }

    @Override
    public String operation() {
        log.info("MessageDecorator 실행");

        //data -> *****data*****
        String result = component.operation();
        String decoResult = "*****" + result + "*****";
        log.info("MessageDecorator 꾸미기 적용 전={}, 적용 후={}", result, decoResult);
        return decoResult;
    }
}

@Slf4j
public class DecoratorPatternTest {
    
    @Test
    void decorator2() {
        Component realComponent = new RealComponent();
        Component messageDecorator = new MessageDecorator(realComponent);
        Component timeDecorator = new TimeDecorator(messageDecorator);
        DecoratorPatternClient client = new DecoratorPatternClient(timeDecorator);
        client.execute();
    }
}
```

데코레이션 패턴을 적용한 이후 클래스 의존 관계와 런타임 시 객체 의존 관계는 아래와 같다.

<img width="804" alt="image" src="https://user-images.githubusercontent.com/53935439/209675219-611e8fbf-d59d-43cb-b377-6879bcfbdbbe.png">

클라이언트의 입장에서는 인터페이스에 의존해서 함수를 호출할 뿐이지만 중간에 대리자(proxy) 2개가 추가되어
부가 기능 2개를 수행 한 이후 핵심 기능을 수행하게 된다.

기존 서버 코드의 수정 없이 데코레이션 패턴을 적용하여 부가 기능을 추가할 수 있게 되었다.
즉, 유연하게 기능 확장을 할 수 있게 되었다.

# 클래스 기반 프록시

앞서 두 예제 모두 인터페이스 기반으로 프록시를 적용했었다.
인터페이스가 없는 구체 클래스의 경우
프록시를 적용할 때 자바의 다형성을 이용하면 된다.

예시를 통해 알아보자

```java
@Slf4j
public class ConcreteLogic {
    public String operation(){
        log.info("concreteLogic 실행");
        return "Data";
    }
}

public class ConcreteClient {

    private ConcreteLogic concreteLogic;

    public ConcreteClient(ConcreteLogic concreteLogic) {
        this.concreteLogic = concreteLogic;
    }

    public void execute(){
        concreteLogic.operation();
    }
}

public class ConcreteProxyTest {
    @Test
    void noProxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        ConcreteClient concreteClient = new ConcreteClient(concreteLogic);
        concreteClient.execute();
    }
}
```

코드를 보면 알겠지만 클라이언트는 추상화에 의존하고 있지 않고 구체 클래스에 의존하고 있다.
이런 상황에서 만약 코드를 변경하지 않고 부가 기능을 추가하려면 어떻게 해야할까?

자바의 다형성을 이용하면 된다. 구체 클래스를 상속받아 프록시를 만든 뒤 해당 프록시의 인스턴스를
클라이언트에 주입해주면 된다. 그렇게 된다면 클라이언트는 여전히 구체 클래스에 의존하고 있는 상태에서
구체 클래스의 인스턴스가 아닌 구체 클래스를 상속받은 자식 클래스(proxy)의 인스턴스를 주입받아 처리하면 된다.

그렇게 되면 코드를 수정하지 않고도 프록시를 활용하여 부가 기능을 추가할 수 있게 된다.

```java
@Slf4j
public class TimeProxy extends ConcreteLogic{

    private ConcreteLogic target;

    public TimeProxy(ConcreteLogic target) {
        this.target = target;
    }

    @Override
    public String operation() {
        log.info("timeDecorator 실행");
        long startTime = System.currentTimeMillis();

        // 실제 타겟 메서드 호출
        String result = target.operation();

        long endTime = System.currentTimeMillis();
        log.info("runningTime = {}",endTime-startTime);
        return result;
    }
}

public class ConcreteProxyTest {
    @Test
    void proxy() {
        ConcreteLogic concreteLogic = new ConcreteLogic();
        TimeProxy timeProxy = new TimeProxy(concreteLogic);
        ConcreteClient concreteClient = new ConcreteClient(timeProxy);
        concreteClient.execute();
    }
}
```