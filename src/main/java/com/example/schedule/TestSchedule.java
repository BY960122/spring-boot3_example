package com.example.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.annotation.CustomDataSource;
import com.example.model.PersonModel;
import com.example.tool.JsonTools;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

/**
 * @author: BYDylan
 * @date: 2022/2/20
 * @description: 测试定时调度
 * lockAtLeastFor: 持有锁最少时间
 * lockAtMostFor: 持有锁最长时间
 */
@Component
public class TestSchedule {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSchedule.class);
    private final PersonModel personModel;

    public TestSchedule(PersonModel personModel) {
        this.personModel = personModel;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    @SchedulerLock(name = "scheduler_shedlock_test", lockAtLeastFor = "PT1S", lockAtMostFor = "PT30S")
    @CustomDataSource()
    public void getEntity() {
        LOGGER.info("test schedule: {}", JsonTools.toJsonString(personModel));
    }
}
