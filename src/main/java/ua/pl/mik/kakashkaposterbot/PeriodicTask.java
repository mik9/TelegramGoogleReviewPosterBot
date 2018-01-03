package ua.pl.mik.kakashkaposterbot;

import com.google.api.services.androidpublisher.model.Review;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ua.pl.mik.kakashkaposterbot.bot.TelegramReview;
import ua.pl.mik.kakashkaposterbot.db.Database;
import ua.pl.mik.kakashkaposterbot.db.models.App;
import ua.pl.mik.kakashkaposterbot.utils.TelegramUtils;

import java.time.LocalDateTime;
import java.util.List;

public class PeriodicTask implements Runnable {

    private App app;
    private static final Logger logger = LoggerFactory.getLogger(PeriodicTask.class);

    public PeriodicTask(App app) {
        this.app = app;
    }

    @Override
    public void run() {
        try {
            runInternal();
        } catch (Throwable e) {
            LoggerFactory.getLogger(PeriodicTask.class)
                    .error("Error in review processing: ", e);
        }
    }

    private void runInternal() {
        logger.debug("Running update task for: {}", app.packageName);
        List<Review> reviews = GoogleApi.getNewReviews(app.packageName, app.keyFilePath, app.lastReviewId);
        logger.debug("Got {} new reviews", reviews.size());
        if (!reviews.isEmpty()) {
            app.lastReviewId = reviews.get(0).getReviewId();
        }
        reviews.forEach(review -> logger.debug(review.toString()));
        reviews.stream()
                .map(review1 -> new TelegramReview(review1, app.packageName))
                .peek(review -> {
                    if (review.text != null) {
                        review.text = GoogleApi.translate(review.text, app.translateLanguage);
                    }
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
