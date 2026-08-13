package com.smartmed.app.data.api;

import com.smartmed.app.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit API client.
 * Configures OkHttp with logging and Firebase token interceptor.
 */
public class ApiClient {

    private static volatile Retrofit retrofit = null;
    private static volatile Retrofit aiRetrofit = null;

    /**
     * Returns the main backend Retrofit instance.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    // Logging interceptor for debugging
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new FirebaseTokenInterceptor())
                            .addInterceptor(logging)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .build();

                    retrofit = new Retrofit.Builder()
                            .baseUrl(Constants.BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * Returns the AI service Retrofit instance.
     */
    public static Retrofit getAiClient() {
        if (aiRetrofit == null) {
            synchronized (ApiClient.class) {
                if (aiRetrofit == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                    OkHttpClient client = new OkHttpClient.Builder()
                            .addInterceptor(new FirebaseTokenInterceptor())
                            .addInterceptor(logging)
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS) // AI may take longer
                            .build();

                    aiRetrofit = new Retrofit.Builder()
                            .baseUrl(Constants.AI_BASE_URL)
                            .client(client)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }
        return aiRetrofit;
    }

    /**
     * Returns the main API service.
     */
    public static SmartMedApi getApiService() {
        return getClient().create(SmartMedApi.class);
    }

    /**
     * Returns the AI API service.
     */
    public static AiApi getAiApiService() {
        return getAiClient().create(AiApi.class);
    }

    /**
     * Resets the Retrofit instances (useful for URL changes or logout).
     */
    public static void reset() {
        retrofit = null;
        aiRetrofit = null;
    }
}
