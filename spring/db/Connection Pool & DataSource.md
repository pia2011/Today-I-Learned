# 커넥션 풀

![image](https://github.com/pia2011/stock/assets/53935439/ab4fdb34-eb2a-4b51-abf0-20718d571a0f)

DB와 애플리케이션 간 통신할 수 있는 수단이 필요하다. JDBC 에서는 getConnection 이라는 추상화된 메서드를 통해
Connection(DB 연결 객체) 을 받아 애플리케이션에서 활용할 수 있도록 지원한다.

헌데 이 Connection 획들을 하기 위해서는 아래와같이 복잡한 과정을 거친다.

1. 커넥션 조회
2. DB와 TCP 연결
3. 부가정보(ID, PWD) 전달
4. 인증 완료 시 DB 세션 생성
5. DB 커넥션 응답 수신
6. DB 드라이버가 Connection 객체 생성 후 Client 에 return

시간도 많이 소모된다. SQL 실행에도 시간이 소요되는데 Connection 을 생성하기 위한 리소스를 매번 사용하는것은
비효율적인 일이다. 응답 속도에도 영향을 미칠 수 있다. 

문제를 해결하기 위해 Connection Pool 개념이 등장했다.

## Connection Pool

![image](https://github.com/pia2011/stock/assets/53935439/1601894d-a978-4366-80aa-46dae86bcbb1)

Connection 을 관리하는 Pool 로 생성 비용이 많이 드는 Connection 을 미리 생성해두고, 이를 관리하여
애플리케이션에서 필요할 때 전달해주고 반환해주는 식으로 운영한다.

![image](https://github.com/pia2011/stock/assets/53935439/4a42d844-77d0-4b5a-82f4-4075860c1139)

위와 같이 미리 연결되어 있다. 

### HikariCP

> 이점이 크기 때문에 실무에서는 기본적으로 Connection Pool 을 활용한다. 커넥션 풀 오픈소스는
> 여러가지가 있지만 대부분 성능과 편리함 측면에서 레거시 프로젝트가 아닌 이상 대부분 HikariCP 를 사용한다.
> ( 스프링 부트를 사용하면 기본 제공 )

# DataSource


Connection 을 얻는 방법은 JDBC 의 `DriveManager`를 직접 사용하거나, Connection Pool 을 사용하는 등
다양한 방법이 존재한다.

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/518ed8d9-2a9f-467f-b3c4-67d715f73d82)


## Connection 획득 방법 추상화

커넥션을 획득하는 방법을 추상화한 interface 인 Datasource 를 의존하게되면 획득 방법이 변경되었을 때 기존 어플리케이션
코드를 변경하지 않고 변경할 수 있다. 즉 구현체만 갈아 끼면 변경가능하다. (Java 기본 제공 : `javax.sql.DataSource`)

![image](https://github.com/pia2011/Today-I-Learned/assets/53935439/c392f578-a849-45c4-80f4-4be12c1856d6)

애플리케이션 개발을 하다보면 설정은 한 곳(변경점)에서 하지만 사용은 수 많은 곳에서 하게된다.

```java
@Slf4j
public class ConnectionTest {

    // 설정 & 사용
    @Test
    void driverManager() throws Exception {
        // DriverMananger : Connection 획득마다 인자를 넘겨야함 (BAD)
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);

        log.info("connection = {} , classes = {}", con1, con1.getClass());
        log.info("connection = {} , classes = {}", con2, con2.getClass());
    }

    // 설정
    @Test
    void dataSourceDriverManager() throws Exception{
        // DriverManagerDataSource : 초기 세팅에만 설정값을 넘기고 사용 (GOOD)
        // 설정과 사용을 분리하여 변경점이 발생했을 때 좀 더 유연하게 대처 가능 (유지보수 원활)
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    // 설정
    @Test
    void dataSourceConnectionPool() throws Exception{
        // HikariDataSource 
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("myPool");
        useDataSource(dataSource);
    }

    // 사용
    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("connection = {} , classes = {}", con1, con1.getClass());
        log.info("connection = {} , classes = {}", con2, con2.getClass());
    }
}
```