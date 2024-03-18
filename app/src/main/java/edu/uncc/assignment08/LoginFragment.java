package edu.uncc.assignment08;

import android.app.AlertDialog;
import android.content.Context;
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

import edu.uncc.assignment08.databinding.FragmentLoginBinding;
import edu.uncc.assignment08.models.AuthResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginFragment extends Fragment {

    public static final String TAG = "debug";

    FragmentLoginBinding binding;

    public LoginFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                if(email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid password!", Toast.LENGTH_SHORT).show();
                } else {
                    Login(email, password);
                }
            }
        });

        binding.buttonCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createNewAccount();
            }
        });

        getActivity().setTitle(R.string.login_label);
    }

    //  Make a HTTP request to /login API, receive a RESPONSE. Evaluate it, if token is present, create a AuthResponse object and send it to MainActivity via authCompleted().
    private void Login(String email, String password) {

        OkHttpClient client = new OkHttpClient();

        // Build request body
        RequestBody requestBody = new FormBody.Builder()
                .add("email", email)
                .add("password", password)
                .build();

        // Build request with api url
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts/login")
                .post(requestBody)
                .build();

        // Make async call to performs the network operation on a background thread without blocking the main UI thread.
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // Get the response sent back from the api request as a string
                String responseBody = response.body().string();

                // Successful response; Status code 200 OK
                if (response.isSuccessful()) {
                    try {
                        // Convert the string body into a JSON object
                        JSONObject loginResponse = new JSONObject(responseBody);
                        // Create Authorization Response and send it the successful JSON response (to be parsed by the class constructor)
                        AuthResponse authResponse = new AuthResponse(loginResponse);
                        ShowToastOnUIThread("Login Successful!");
                        mListener.authCompleted(authResponse);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d(TAG, "API Success Response! " + responseBody);
                } else {
                    try {
                        // Create JSON object for the failed response back
                        JSONObject failedResponse = new JSONObject(responseBody);
                        // Extract the 'message'
                        String failedLoginMsg = failedResponse.getString("message");
                        // Show the 'message' as Toast on the UI thread
                        ShowToastOnUIThread(failedLoginMsg);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d(TAG, "API Failed Response " + responseBody);
                }
            }
        });
    }

    // Toast Message on UI Thread
    private void ShowToastOnUIThread (String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


    // Interface
    LoginListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (LoginListener) context;
    }

    interface LoginListener {
        void createNewAccount();
        void authCompleted(AuthResponse authResponse);
    }
}