package com.example.l3_20202137;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
public interface TriviaApiServicio {
    @GET("api_category.php")
    Call<CategoryResponse> getCategories();


    @GET("api.php")
    Call<Answer> getQuestions(
            @Query("amount") int amount,
            @Query("category") int category,
            @Query("difficulty") String difficulty,
            @Query("type") String type
    );
}