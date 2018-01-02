package ua.pl.mik.kakashkaposterbot;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.androidpublisher.AndroidPublisher;
import com.google.api.services.androidpublisher.AndroidPublisherScopes;
import com.google.api.services.androidpublisher.model.Review;
import com.google.api.services.androidpublisher.model.ReviewsListResponse;
import com.google.gson.JsonElement;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.Nonnull;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GoogleApi {
    private static OkHttpClient client = new OkHttpClient.Builder()
            .build();

    public static void init() {
    }

    private static AndroidPublisher androidPublisherFactory(String keyPath) {
        AndroidPublisher androidPublisher;
        try {
            GoogleCredential credential = GoogleCredential.fromStream(new FileInputStream(keyPath))
                    .createScoped(Collections.singleton(AndroidPublisherScopes.ANDROIDPUBLISHER));
            androidPublisher = new AndroidPublisher.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    credential)
                    .setApplicationName("Comment Bot")
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Can't initializa GoogleApi", e);
        }

        return androidPublisher;
    }

    @Nonnull
    public static List<Review> getNewReviews(String packageName, String keyPath, String lastReviewId) {
        List<Review> reviews = new ArrayList<>();
        String token = null;
        try {
            do {
                AndroidPublisher.Reviews.List call = androidPublisherFactory(keyPath).reviews()
                        .list(packageName);
                if (token != null) {
                    call.setToken(token);
                }
                ReviewsListResponse response = call.execute();
                if (response.getReviews() != null) {
                    for (Review review : response.getReviews()) {
                        if (!Objects.equals(review.getReviewId(), lastReviewId)) {
                            reviews.add(review);
                            if (lastReviewId == null) {
                                break;
                            }
                            continue;
                        }
                        break;
                    }
                    if (response.getTokenPagination() != null) {
                        token = response.getTokenPagination().getNextPageToken();
                    }
                }
            } while (token != null && lastReviewId != null);
        } catch (IOException e) {
            throw new RuntimeException("Can't download reviews", e);
        }
        return reviews;
    }

    public static String translate(String text, String targenLanguage) {
        String url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl=%s&dt=t&q=%s";
        try {
            Request request = new Request.Builder()
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_2) AppleWebKit/602.3.12 (KHTML, like Gecko) Version/10.0.2 Safari/602.3.12")
                    .url(String.format(url, targenLanguage, URLEncoder.encode(text, "UTF-8")))
                    .build();
            Response response = client.newCall(request).execute();
            byte[] bytes = response.body().bytes();
            String body = new String(bytes);
            JsonElement element = new com.google.gson.JsonParser().parse(body);

            StringBuilder builder = new StringBuilder();
            element.getAsJsonArray().get(0).getAsJsonArray()
                    .forEach(jsonElement -> builder.append(jsonElement.getAsJsonArray().get(0).getAsString()));

            return builder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
