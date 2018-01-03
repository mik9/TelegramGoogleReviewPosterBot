package ua.pl.mik.kakashkaposterbot.bot;

import com.google.api.services.androidpublisher.model.Review;

import javax.annotation.Nullable;

import static ua.pl.mik.kakashkaposterbot.utils.TelegramUtils.androidApiVersionToString;

public class TelegramReview {
    @Nullable
    public String text;
    private final int stars;
    private final String authorName;
    @Nullable
    private final String androidVersion;
    @Nullable
    private final String deviceName;
    private final String appName;

    public TelegramReview(Review review, String appName) {
        text = review.getComments().get(0).getUserComment().getText();
        stars = review.getComments().get(0).getUserComment().getStarRating();
        authorName = review.getAuthorName();
        androidVersion = androidApiVersionToString(review.getComments().get(0).getUserComment().getAndroidOsVersion());
        deviceName = review.getComments().get(0).getUserComment().getDevice();
        this.appName = appName;
    }

    public String render() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(appName)
                .append(":\n");
        stringBuilder.append(authorName);
        if (deviceName != null) {
            stringBuilder.append(" from ")
                    .append(deviceName);
        }
        if (androidVersion != null) {
            stringBuilder.append(" on Android ")
                    .append(androidVersion);
        }
        stringBuilder.append("\n");
        for (int i = 0; i < stars; i++) {
            stringBuilder.append("⭐️");
        }
        for (int i = stars; i < 5; i++) {
            stringBuilder.append("☆");
        }
        if (text != null) {
            stringBuilder.append("\n");
            stringBuilder.append(text);
        }
        return stringBuilder.toString();
    }
}
