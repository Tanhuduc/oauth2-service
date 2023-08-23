package vn.vnpay.cronjob;

import it.sauronsoftware.cron4j.Scheduler;
import lombok.extern.slf4j.Slf4j;
import vn.vnpay.cronjob.task.InitInstanceTask;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class InitInstanceCronjob {
    private static final String SCHEDULING_PATTERN = "*/5 * * * *";

    private InitInstanceCronjob() {
    }

    public static void start() {
        try {
            log.info("Start cronjob init instance");
            Scheduler scheduler = new Scheduler();
            scheduler.schedule(SCHEDULING_PATTERN, InitInstanceTask.getInstance());
            scheduler.start();
        } catch (Exception e) {
            log.error("Cronjob start fails, exception: ", e);
        }
    }
}
