package com.unibuc.badhabbits;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unibuc.badhabbits.api.QuotesApi;
import com.unibuc.badhabbits.client.RetrofitClient;
import com.unibuc.badhabbits.model.Quote;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TricksFragment extends Fragment {

    private TextView quoteTextView;
    private TextView authorTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tricks, container, false);

        quoteTextView = view.findViewById(R.id.textViewQuote);
        authorTextView = view.findViewById(R.id.textViewAuthor);

        fetchQuote(); // Call method to fetch quote from API

        return view;
    }

    private void fetchQuote() {
        QuotesApi apiService = RetrofitClient.getClient().create(QuotesApi.class);
        Call<Quote[]> call = apiService.getQuote();

        call.enqueue(new Callback<Quote[]>() {
            @Override
            public void onResponse(Call<Quote[]> call, Response<Quote[]> response) {
                if (response.isSuccessful() && response.body() != null && response.body().length > 0) {
                    Quote quote = response.body()[0]; // Get the first quote
                    displayQuote(quote);
                } else {
                    // Handle unsuccessful responses or errors
                    Toast.makeText(getActivity(), "Failed to fetch quote", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Quote[]> call, Throwable t) {
                // Handle network errors
                Toast.makeText(getActivity(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayQuote(Quote quote) {
        quoteTextView.setText(quote.getQuoteText());
        authorTextView.setText("- " + quote.getAuthor());
    }
}

