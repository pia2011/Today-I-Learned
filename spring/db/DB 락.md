# DB 락

### 멀티 스레드 환경에서 동시에 한 데이터를 수정할 때 발생할 수 있는 문제

세션을 통해 트랜잭션을 시작하고, 데이터를 수정하는 동안 다른 세션이 동시에 같은 데이터를 
수정하게 되면 트랜잭션이 지켜야할 원칙 중 원자성이 깨지게된다.

이를 미연에 방지하기 위해서는 세션이 트랜잭션을 시작하고 데이터를 수정하는 작업이 마무리 되기 전(Rollback or Commit)에
다른 세션이 해당 데이터를 수정할 수 없도록 제약을 걸어야한다. 이 개념이 Lock 이다.

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/cf7579e8-81c0-4557-b783-7f7f07b4cf8a)
![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/8f08d03a-f44d-4ad6-9211-ef7c05e0b291)
![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/6f563206-f876-4af8-b9a4-491c8a065114)
![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/932cbc93-6e68-471e-829e-2bc2bd23bee7)

위와 같이 먼저 요청한 세션이 락을 획득하고 데이터를 수정하여 커밋 또는 롤백이 일어날 때까지 다른 세션은 락을 대기하고
반납된 락을 획득하여 수정 작업을 수행한다. 즉, 하나의 row 를 동시에 수정하는 것은 불가능하다.

### 조회시에도 DB Lock 이 적용될까?

> 일반적인 조회는 락을 사용하지 않는다. 

다만 매우 중요한 로직을 수행하기에 조회 시점에 다른 곳에서의 변경을 차단하기 위해서 조회 시점에 락을 획득하기도 한다. 

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/062b2111-5752-4246-8ca8-441d0550873d)

위와 같이 `select for update` 구문을 활용하여 조회 시점에 DB 락을 획득하여 다른 세션이 해당 row 를 
수정할 수 없게끔 제한할 수 있다.

## 참고

스프링 DB 1편 - 데이터 접근의 핵심 원리(강의)