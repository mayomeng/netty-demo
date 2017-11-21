package mxy.redis.pool;

import io.netty.util.concurrent.Future;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ad.publish.util.BeanFactory;

public class Test {

    public static void main(String[] args) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long startTime = System.currentTimeMillis();
        System.out.println(sdf.format(new Date(startTime)) + " 開始");

        RedisConnectionPool pool = (RedisConnectionPool)BeanFactory.getBean("RedisConnectionPool");
        pool.startup();

        int clientNum = 5000;

        CountDownLatch latch=new CountDownLatch(clientNum);

/*        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        for (;;) {
            final String input = in.readLine();
            final String line = input != null ? input.trim() : null;
            if (line == null || "quit".equalsIgnoreCase(line)) { // EOF or "quit"
                break;
            }
            RedisConnection connection = pool.getConnection();
            if (line.contains("get")) {
                connection.get(line.split(" ")[1]);
            } else if (line.contains("set")) {
                connection.set(line.split(" ")[1], line.split(" ")[2]);
            }
            connection.close();
        }*/

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for (int i = 0 ; i < clientNum ; i++) {
            ConnectionTest test = new ConnectionTest(pool, latch);
            threadPool.execute(test);
        }

        latch.await();
        threadPool.shutdown();
        Future<?> future = pool.destory();
        for(;;) {
            if (future.isSuccess()) {
                long endTime = System.currentTimeMillis();
                System.out.println(sdf.format(new Date(endTime)) + " `終了");
                break;
            }
        }
    }


    static class ConnectionTest implements Runnable {

        private RedisConnectionPool pool;
        private CountDownLatch latch;

        public ConnectionTest(RedisConnectionPool pool, CountDownLatch latch) {
            this.pool = pool;
            this.latch = latch;
        }

        @Override
        public void run() {
            try {

                Random random = new Random();
                int randomNum = random.nextInt(6);

                Object value = pool.get(String.valueOf(randomNum));
                System.out.println(value);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }

    }

}
