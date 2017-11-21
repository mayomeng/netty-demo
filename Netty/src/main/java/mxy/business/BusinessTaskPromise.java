package mxy.business;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class BusinessTaskPromise {

    private String result;

    private BusinessTaskPromise() {

    }

    public static Future<BusinessTaskPromise> newInstance(final String param, ExecutorService threadPool) {

        Callable<BusinessTaskPromise> callable = new Callable<BusinessTaskPromise>() {
            public BusinessTaskPromise call() throws Exception {
                BusinessTaskPromise task = new BusinessTaskPromise();
                task.execut(param);
                return task;
            }
        };

        return threadPool.submit(callable);
    }

    private void execut(String param) throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            System.out.println("execut the message. : " + i);
        }
        Thread.sleep(2000);
        result = "the result of " + param;
    }

    public String outputResult() {
        return result;
    }

    public static void main(String[] args) throws Exception {

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        Future<BusinessTaskPromise> taskFuture = BusinessTaskPromise.newInstance("test", threadPool);

        for (;;) {
            if (taskFuture.isDone()) {
                BusinessTaskPromise task = taskFuture.get();
                String result = task.outputResult();
                System.out.println(result);
                break;
            } else {
                System.out.println("BusinessTask runing...");
            }
        }

        threadPool.shutdown();
    }
}
