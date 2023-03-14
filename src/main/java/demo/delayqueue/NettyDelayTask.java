package demo.delayqueue;


import com.example.tool.TimeTools;
import io.netty.util.HashedWheelTimer;
import io.netty.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class NettyDelayTask {
    public static final Logger LOGGER = LoggerFactory.getLogger(NettyDelayTask.class);
    private static Long startTime = System.currentTimeMillis();


    public static void main(String[] args) {
        // 初始化netty时间轮
        HashedWheelTimer timer = new HashedWheelTimer(1, TimeUnit.SECONDS, 10);

        TimerTask task1 = timeout -> LOGGER.info("already passed {} second, start task1.", TimeTools.costTime(startTime));

        TimerTask task2 = timeout -> LOGGER.info("already passed {} second, start task2.", TimeTools.costTime(startTime));

        TimerTask task3 = timeout -> LOGGER.info("already passed {} second, start task3.", TimeTools.costTime(startTime));

        // 将任务添加到延迟队列
        timer.newTimeout(task1, 0, TimeUnit.SECONDS);
        timer.newTimeout(task2, 3, TimeUnit.SECONDS);
        timer.newTimeout(task3, 10, TimeUnit.SECONDS);
    }
}
