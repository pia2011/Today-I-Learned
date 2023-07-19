import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionEx {
    public static void main(String[] args) {

    }
}

class Table{
    private ReentrantLock lock = new ReentrantLock();
    private Condition forCook = lock.newCondition();
    private Condition forCust = lock.newCondition();

    public void add(){
        // 요리사
        lock.lock();
        try{

            try{
                forCook.await(); // wait()
            } catch(InterruptedException e) {}
        }finally {
            lock.unlock();
        }
        // 음식 추가

        forCust.signal(); // notify()
    }

}