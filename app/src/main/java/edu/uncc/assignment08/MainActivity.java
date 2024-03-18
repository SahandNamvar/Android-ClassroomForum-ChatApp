package edu.uncc.assignment08;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import edu.uncc.assignment08.models.AuthResponse;

public class MainActivity extends AppCompatActivity implements LoginFragment.LoginListener,
        SignUpFragment.SignUpListener, PostsFragment.PostsListener, CreatePostFragment.CreatePostListener {

    public static final String TAG = "debug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // When app launches, check if user Token already exist in Shared Preferences (aka Authorized). If so, No login is required.
        if (isUserAuthenticated()) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerView, new PostsFragment())
                    .commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerView, new LoginFragment())
                    .commit();
        }
    }

    @Override
    public void createNewAccount() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new SignUpFragment())
                .commit();
    }

    @Override // TODO: When we receive the authResponse, store the token and user info in the SharedPreference storage.
    public void authCompleted(AuthResponse authResponse) {
        // Set SharedPreferences after user successfully logs-in for the first time
        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user_id", authResponse.getUser_id());
        editor.putString("user_fullname", authResponse.getUser_fullname());
        editor.putString("token", authResponse.getToken());
        editor.apply();

        // After setting SharedPreferences, launch Posts Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new PostsFragment())
                .commit();
    }

    @Override
    public void gotologin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .commit();
    }

    @Override
    public void logout() {
        // Delete SharedPreferences (user info + token) upon Logout --> Delete user session + token. They can still log back in using email + pass.
        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("user_id");
        editor.remove("user_fullname");
        editor.remove("token");
        editor.apply();

        // After deletion, launch Login Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new LoginFragment())
                .addToBackStack(null)
                .commit();

        Log.d(TAG, "logout: " + "SharedPreferences Deleted!");
    }

    @Override
    public void createPost() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.containerView, new CreatePostFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void goBackToPosts() {
        getSupportFragmentManager().popBackStack();
    }

    // Upon MainActivity start, check if user is authenticated
    private boolean isUserAuthenticated() {
        // Retrieve token and user information from default shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("mySharedPref", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);
        String userFullname = sharedPreferences.getString("user_fullname", null);
        String userId = sharedPreferences.getString("user_id", null);

        // Check if token and user information are present
        return (token != null && userFullname != null && userId != null);
    }
}