package demo.delayqueue;


import com.example.tool.TimeTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/***
 * @author: BYDylan
 * @date:  2022/10/17
 * @description:  DelayQueue 方式来实现延迟任务
 */
public class DelayQueueTest {
    public static final Logger LOGGER = LoggerFactory.getLogger(DelayQueueTest.class);

    public static void main(String[] args) {
        DelayQueue<DelayTask> dq = new DelayQueue<>();
        // 生产者生产一个2秒的延时任务
        new Thread(new ProducerDelay(dq, 2000)).start();
        // 开启消费者轮询
        new Thread(new ConsumerDelay(dq)).start();
    }

    static class ProducerDelay implements Runnable {
        private int delaySecond;
        private DelayQueue<DelayTask> delayQueue;

        ProducerDelay(DelayQueue<DelayTask> delayQueue, int delaySecond) {
            this.delayQueue = delayQueue;
            this.delaySecond = delaySecond;
        }

        @Override
        public void run() {
            for (int index = 1; index < 6; index++) {
                delayQueue.add(new DelayTask(delaySecond, index + ""));
                LOGGER.info("product delay task id: {}", index);
                TimeTools.sleep(TimeTools.ONE_SEC_MILLIS);
            }
        }
    }

    static class ConsumerDelay implements Runnable {
        DelayQueue<DelayTask> delayQueue;

        ConsumerDelay(DelayQueue<DelayTask> delayQueue) {
            this.delayQueue = delayQueue;
        }

        @Override
        public void run() {
            while (true) {
                DelayTask delayTask = null;
                try {
                    delayTask = delayQueue.take();
                } catch (Exception e) {
                    LOGGER.error("comsumer delay task error: ", e);
                }
                // 如果Delay元素存在,则任务到达超时时间
                if (delayTask != null) {
                    LOGGER.info("comsumer delay task id: {}", delayTask.getId());
                } else {
                    TimeTools.sleep(TimeTools.ONE_SEC_MILLIS);
                }
            }
        }
    }

    static class DelayTask implements Delayed {
        String id;

        long delayTime = System.currentTimeMillis();

        DelayTask(long delayTime, String id) {
            this.delayTime = (this.delayTime + delayTime);
            this.id = id;
        }

        /**
         * 获取剩余时间
         */
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        }

        /**
         * 队列里元素的排序依据
         */
        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), o.getDelay(TimeUnit.MILLISECONDS));
        }

        String getId() {
            return id;
        }
    }
}