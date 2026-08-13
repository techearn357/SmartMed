package com.smartmed.app.data.api;

import androidx.annotation.NonNull;

import com.smartmed.app.utils.SharedPrefManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp interceptor that adds the Firebase ID token to every request.
 * This allows the backend to verify the user's identity.
 */
public class FirebaseTokenInterceptor implements Interceptor {

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        String token = SharedPrefManager.getInstance().getAuthToken();

        if (token != null && !token.isEmpty()) {
            Request authorized = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .build();
            return chain.proceed(authorized);
        }

        return chain.proceed(original);
    }
}
