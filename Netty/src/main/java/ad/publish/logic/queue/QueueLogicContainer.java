package ad.publish.logic.queue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ad.publish.info.AdColumn;
import ad.publish.logic.CallBackHandlerForLogic;
import ad.publish.logic.LogicContainer;
import ad.publish.logic.PublishProducer;
import ad.publish.logic.unit.LogicUnit;
import ad.publish.task.Task;
import ad.publish.util.AppProperties;
import ad.publish.util.BeanFactory;

@Component("QueueContainer")
public class QueueLogicContainer implements LogicContainer {

    private static Logger log = LoggerFactory.getLogger(QueueLogicContainer.class);

    private volatile boolean isShutdown = false;

    @Autowired
    @Qualifier("ProducerQueue")
    private PublishProducer taskQueue;

    private ExecutorService threadPool;

    @Override
    public void startup() {
        int threadNum = Integer.parseInt(AppProperties.getValue("logic.thread.num"));
        threadPool = Executors.newFixedThreadPool(threadNum);
        for (int i = 0; i < threadNum; i++) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    dispose();
                }

            });
        }
    }

    @Override
    public void shutdown() throws Exception {
        threadPool.shutdown();
        isShutdown = true;
    }

    private void dispose() {

        try {

            for(;;) {

                if (isShutdown) {
                    break;
                }

                final Task task = taskQueue.poll();
                if (task == null) {
                    continue;
                }

                LogicUnit logicUnit = (LogicUnit)BeanFactory.getBean("LogicUnit");
                logicUnit.setTask(task);

                try {
                    AdColumn adColumn = (AdColumn)task.getParams();
                    CallBackHandlerForLogic callBackHandler = new CallBackHandlerForLogic();
                    callBackHandler.setParams(adColumn.getAdColumnId());
                    task.getContext().writeAndFlush(logicUnit.getResult(), callBackHandler);
                } catch (Exception e) {
                    task.getContext().close();
                    log.error(e.getMessage());
                } finally {

                }



            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            //logicThreadPool.shutdown();
        }

    }

}
