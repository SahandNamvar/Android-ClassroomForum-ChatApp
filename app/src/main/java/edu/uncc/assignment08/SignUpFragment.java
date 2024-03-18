package edu.uncc.assignment08;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.assignment08.databinding.FragmentSignUpBinding;
import edu.uncc.assignment08.models.AuthResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignUpFragment extends Fragment {

    public static final String TAG = "debug";

    FragmentSignUpBinding binding;

    public SignUpFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.create_account_label);

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotologin();
            }
        });

        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.editTextName.getText().toString();
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();

                if(name.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid name!", Toast.LENGTH_SHORT).show();
                } else if(email.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid email!", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()){
                    Toast.makeText(getActivity(), "Enter valid password!", Toast.LENGTH_SHORT).show();
                } else {
                    // If all fields are entered, Make a POST HTTP request with the provided data
                    SingUp(name, email, password);
                }
            }
        });
    }

    // Signup Api
    private void SingUp(String name, String email, String password) {

        OkHttpClient client = new OkHttpClient();

        RequestBody requestBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts/signup")
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

                if (response.isSuccessful()) {
                    try {
                        JSONObject responseBodyJson = new JSONObject(responseBody);
                        AuthResponse authResponse = new AuthResponse(responseBodyJson);
                        ShowToastOnUIThread("New User Added: (user_id) " + authResponse.getUser_id());
                        mListener.authCompleted(authResponse);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d(TAG, "API User Created Response: " + responseBody);
                } else { // This block is almost never executed because unless the user has entered some text in all 3 fields they can click submit.
                    try {
                        JSONObject failedResponseJson = new JSONObject(responseBody);
                        String failedMsg = failedResponseJson.getString("message");
                        ShowToastOnUIThread(failedMsg);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d(TAG, "API User Not Created Response: " + responseBody);
                }
            }
        });
    }

    // Toast on UI Thread outside Async HTTP request call
    private void ShowToastOnUIThread (String msg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Interface
    SignUpListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SignUpListener) context;
    }

    interface SignUpListener {
        void gotologin();
        void authCompleted(AuthResponse authResponse);
    }
}