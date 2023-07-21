# JDBC

## JDBC는 왜 등장했을까

애플리케이션을 개발할 때 중요한 데이터는 대부분 DB 에 보관한다.

![image](https://github.com/pia2011/stock/assets/53935439/6c8dd6fe-cfbb-42d9-be86-8c07c689106d)

다음과 같은 순서로 동작한다.

![image](https://github.com/pia2011/stock/assets/53935439/b88c5fa7-2ff7-4a63-bd10-31b343b72933)

1. 커낵션 연결 (TCP)
2. SQL 쿼리 전달
3. 결과 응답

--- 

과거에는 DB 마다 사용방법이 달랐기 때문에 DB 를 변경하면 비즈니스 코드도 함께 변경해야됐고 더불어
새롭게 해당 DB 사용법을 학습해야했다.

이런 문제를 해결하기 위해 JDBC 라는 자바 표준이 등장하게됐다.

## Java DataBase Connectivity 표준 인터페이스

- 자바에서 DB에 접속할 수 있도록 하는 자바 API 

![image](https://github.com/pia2011/stock/assets/53935439/fcf15166-7cca-4cf0-9650-e82b997799f6)

다음과 같이 표준을 정해놓고 DIP 통해 의존성을 역전시켜 DB 연결방법을 추상화해서 사용한다. 즉, 코드가
추상화에 의존하기 때문에 변경점이 발생하지 않는다.

![image](https://github.com/pia2011/stock/assets/53935439/499344a1-16b2-4d64-83e7-3137f482b26a)

> 즉 표준화해서 더욱 편리하게 DB 기술을 활용할 수 있게 됐다. 물론 각각의 DB 마다 SQL은 다르기 때문에
> JDBC 코드는 변경하지 않더라도 페이징 쿼리 같은 사용법은 다르다. 즉, SQL 은 바꿔야한다. 이를 해결하기
> 위함이 방언을 지원하는 JPA 와 같은 기술이다.

---

## JDBC & SQL Mapper & ORM

JDBC 를 직접 사용할 수도 있고, SQL Mapper, ORM 기술 등을 활용할 수도 있다.

### SQL Mapper

![image](https://github.com/pia2011/stock/assets/53935439/8937f9a3-71da-419b-81e0-3ec72d0801ab)

- 장점
  - JDBC 편리하게 사용
  - JDBC 반복 코드 제거
- 단점
  - SQL 직접 작성

> JdbcTemplate, MyBatis 등이 있다.

### ORM

![image](https://github.com/pia2011/stock/assets/53935439/7224e539-7b40-4b96-8f61-71beff96a157)

객체를 DB 테이블과 매핑해주는 기술로 반복적인 SQL 작성을 줄여주고 동적으로 SQL 을 만들어 실행해준다.
더불어 방언을 지원해줘서 DB 마다 SQL 을 따로 작성해줄 필요가 없다. 생산성 UP, But 학습곡선 High

> JPA, 하이버네이트, 이클립스링크

## 정리

어쨋든 SQL Mapper & ORM 기술 모두 JDBC 를 의존하기 때문에 어떻게 돌아가는지 기본 원리를 알아두어야 
장애 발생 원인을 파악하고 해결할 수 있다. 자바 개발자라면 꼭 알아둬야하는 필수 기술이다.



