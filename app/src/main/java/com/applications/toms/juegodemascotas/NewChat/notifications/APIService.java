package com.applications.toms.juegodemascotas.NewChat.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAA7U730Mk:APA91bGSVOWQlAzckbDc6EohoC4uL1GlSdXknZwuN7-YUclRGX_qtbqiGNzU9otme7G6QGaHITrwOsJACw8vZqWYiw-wr7ZsjXj2spXMZtWVrjYd4YQMm0E0mKTJEtNBf18F5UhW-iHz"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
