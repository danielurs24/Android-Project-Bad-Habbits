package com.unibuc.badhabbits.api;

import com.unibuc.badhabbits.model.Quote;

import retrofit2.Call;
import retrofit2.http.GET;
public interface QuotesApi {
    @GET("random")
    Call<Quote[]> getQuote();
}
