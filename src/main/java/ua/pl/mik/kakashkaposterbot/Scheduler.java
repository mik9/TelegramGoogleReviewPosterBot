package ua.pl.mik.kakashkaposterbot;

import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private static HashMap<App, ScheduledFuture> appFutureMap = new HashMap<>();

    private static final long PERIOD = 2; // min

    public static void init() {
        Database.get().listAppsByChatId()
                .stream()
                .filter(app -> app.enabled)
                .forEach(Scheduler::scheduleApp);
    }

    public static void scheduleAppNow(App app) {
        scheduleAppInternal(app, 0);
    }

    public static void scheduleApp(App app) {
        long delay;
        if (app.lastCheckTime == null) {
            delay = 0;
        } else {
            LocalDateTime current = LocalDateTime.now();
            Duration between = Duration.between(app.lastCheckTime, current);
            delay = PERIOD - between.toMinutes();
            if (delay < 0) {
                delay = 0;
            }
        }

        scheduleAppInternal(app, delay);
    }

    private static void scheduleAppInternal(App app, long delay) {
        System.out.println("Scheduling app: " + app.packageName + " to run in " + delay + " minutes");
        ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(new PeriodicTask(app), delay, PERIOD, TimeUnit.MINUTES);
        appFutureMap.put(app, future);
    }


    public static void unSchedule(App app) {
        ScheduledFuture future = appFutureMap.remove(app);
        if (future != null) {
            future.cancel(false);
        }
    }
}
