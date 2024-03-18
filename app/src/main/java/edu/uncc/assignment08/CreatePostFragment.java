package edu.uncc.assignment08;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.assignment08.databinding.FragmentCreatePostBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreatePostFragment extends Fragment {

    public static final String TAG = "debug";

    FragmentCreatePostBinding binding;

    public CreatePostFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreatePostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.create_post_label);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.goBackToPosts();
            }
        });

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String postText = binding.editTextPostText.getText().toString();
                if(postText.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid post !!", Toast.LENGTH_SHORT).show();
                } else {
                    CreatePost(postText);
                }
            }
        });
    }

    // Create a post (text based post) API
    private void CreatePost(String postText) {

        OkHttpClient client = new OkHttpClient();

        // Get user Token from sharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        Log.d(TAG, "CreatePostsFragment token: " + token);

        // Build request body
        RequestBody requestBody = new FormBody.Builder()
                .add("post_text", postText)
                .build();

        // Build the GET request with the user token for authorization in the request Header.
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts/create")
                .addHeader("Authorization", "BEARER " + token)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String responseBody = response.body().string();
                String successMsg, failMsg;

                if (response.isSuccessful()) {

                    try {
                        JSONObject responseBodyJson = new JSONObject(responseBody);
                        successMsg = responseBodyJson.getString("message");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d(TAG, "API Create Post Success Response! " + responseBody);
                    ShowToastOnUIThreat(successMsg);
                    mListener.goBackToPosts();
                } else {

                    try {
                        JSONObject responseBodyJson = new JSONObject(responseBody);
                        failMsg = responseBodyJson.getString("message");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d(TAG, "API Failed to Create Post Response! " + responseBody);
                    ShowToastOnUIThreat(failMsg);
                }
            }
        });
    }

    private void ShowToastOnUIThreat (String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Interface
    CreatePostListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (CreatePostListener) context;
    }

    interface CreatePostListener {
        void goBackToPosts();
    }
}