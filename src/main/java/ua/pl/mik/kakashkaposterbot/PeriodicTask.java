package ua.pl.mik.kakashkaposterbot;

import com.google.api.services.androidpublisher.model.Review;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramReview;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.time.LocalDateTime;
import java.util.List;

public class PeriodicTask implements Runnable {

    private App app;

    public PeriodicTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        try {
            runInternal();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void runInternal() {
        System.out.println("Running update task for: " + app.packageName);
        List<Review> reviews = GoogleApi.getNewReviews(app.packageName, app.keyFilePath, app.lastReviewId);
        System.out.println("Got " + reviews.size() + " new reviews");
        if (!reviews.isEmpty()) {
            app.lastReviewId = reviews.get(0).getReviewId();
        }
        reviews.stream()
                .map(TelegramReview::new)
                .map(review -> {
                    if (review.text != null) {
                        review.text = GoogleApi.translate(review.text, app.translateLanguage);
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
