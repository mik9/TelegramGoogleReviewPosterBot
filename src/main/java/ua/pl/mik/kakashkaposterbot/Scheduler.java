package ua.pl.mik.kakashkaposterbot;

import com.google.api.services.androidpublisher.model.Review;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramReview;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    private static ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);
    private static HashMap<App, ScheduledFuture> appFutureMap = new HashMap<>();

    private static final long PERIOD = 2; // min

    public static class Task implements Runnable {

        private App app;

        public Task(App app) {
            this.app = app;
        }

        @Override
        public void run() {
            System.out.println("Running update task for: " + app.packageName);
            List<Review> reviews = Collections.emptyList();
            try {
                reviews = GoogleApi.getNewReviews(app.packageName, app.keyFilePath, app.lastReviewId);
            } catch (Exception e) {
                System.out.println("Reviews downloading failed.");
            }
            System.out.println("Got " + reviews.size() + " new reviews");
            if (!reviews.isEmpty()) {
                app.lastReviewId = reviews.get(0).getReviewId();
            }
            reviews.stream()
                    .map(TelegramReview::new)
                    .map(review -> {
                        if (review.text != null) {
                            review.text = GoogleApi.translate(review.text);
                        }
                        return review;
                    })
                    .forEach(s -> {
                        try {
                            TelegramUtils.sendSimpleTextMessage(app.chatId, s.render());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    });

            app.lastCheckTime = LocalDateTime.now();
            Database.get().saveApp(app);
        }
    }

    public static void init() {
        Database.get().listApps()
                .stream()
                .filter(app -> app.enabled)
                .forEach(Scheduler::scheduleApp);
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
        System.out.println("Scheduling app: " + app.packageName + " to run in " + delay + " minutes");
        ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(new Task(app), delay, PERIOD, TimeUnit.MINUTES);
        appFutureMap.put(app, future);
    }


    public static void unSchedule(App app) {
        ScheduledFuture future = appFutureMap.remove(app);
        if (future != null) {
            future.cancel(false);
        }
    }
}
