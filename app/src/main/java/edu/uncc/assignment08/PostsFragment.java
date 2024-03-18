package edu.uncc.assignment08;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edu.uncc.assignment08.databinding.FragmentPostsBinding;
import edu.uncc.assignment08.databinding.PostRowItemBinding;
import edu.uncc.assignment08.models.Post;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostsFragment extends Fragment {

    public static final String TAG = "debug";

    // Local variables
    FragmentPostsBinding binding;
    PostsAdapter postsAdapter;
    ArrayList<Post> mPosts = new ArrayList<>();

    int PageToFetch = 1; // The '/posts' endpoint URL requires a page as a parameter (in any GET fetch calls, always fetch page 1)
    int TotalPagesToDisplay; // To calculate how many pages to render --> Math.ceil(totalCount/10)

    String token = null;
    SharedPreferences sharedPreferences = null; // Globally declare one main instance of SharedPreferences to be user throughout the fragment

    public PostsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPostsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(R.string.posts_label);

        // Get user Token from sharedPreferences (This Fragment is only visible IF and only IF SharedPreferences are Set either through Creating a new account or logging-in - Hence, when this Fragment starts, there is already a 'MySharedPref' PS with the user info.)
        sharedPreferences = requireActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("token", null);
        Log.d(TAG, "PostsFragment token: " + token);
        binding.textViewTitle.setText("Greetings, " + sharedPreferences.getString("user_fullname", null));

        // OnClick button Create Post: launch CreatePostFragment. Upon return (pop the backstack), Make GET a call to the API and fetch data again.
        binding.buttonCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.createPost();
                GetPosts(String.valueOf(PageToFetch), token);
            }
        });

        // OnClick button Logout - Delete Shared Preferences local storage (token + other user info)
        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.logout();
            }
        });

        // Set Layout Manager and Adapter
        binding.recyclerViewPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        postsAdapter = new PostsAdapter();
        binding.recyclerViewPosts.setAdapter(postsAdapter);

        // OnClick Previous Page - Decrement 'PageToFetch', make a new API GET request with the new Page Number to fetch data,
        // notify adapter of changes (new data fetched from different page, populated into arrayList, hence Adapter must be notified of
        // the change to render the newly populated list of posts)
        binding.imageViewPrevious.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                PageToFetch--;
                if (PageToFetch < 1) {
                    PageToFetch = TotalPagesToDisplay;
                }
                GetPosts(String.valueOf(PageToFetch), token);
                postsAdapter.notifyDataSetChanged();
            }
        });

        // OnClick Next Page
        binding.imageViewNext.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View v) {
                PageToFetch++;
                if (PageToFetch > TotalPagesToDisplay) {
                    PageToFetch = 1;
                }
                GetPosts(String.valueOf(PageToFetch), token);
                postsAdapter.notifyDataSetChanged();
            }
        });

        // Make the MAIN Get Request to Fetch posts upon onViewCreated
        GetPosts(String.valueOf(PageToFetch), token);
    }

    // GET posts Api
    @SuppressLint("SetTextI18n")
    private void GetPosts(String pageToFetch, String token) {

        OkHttpClient client = new OkHttpClient();

        // Build a dynamic URL with changing Query parameter
        HttpUrl postsURL = HttpUrl.parse("https://www.theappsdr.com/posts")
                .newBuilder()
                .addQueryParameter("page", pageToFetch)
                .build();

        // Build the GET request with the user token for authorization in the request Header.
        Request request = new Request.Builder()
                .url(postsURL)
                .addHeader("Authorization", "BEARER " + token)
                .build();

        // Async Fetch request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String responseBody = response.body().string();

                if (response.isSuccessful()){
                    try {
                        // Get the Full response object
                        JSONObject responseJsonObject = new JSONObject(responseBody);

                        // Get the Posts array of objects (post objects)
                        JSONArray postsArray = responseJsonObject.getJSONArray("posts");

                        // Get count of total posts, current page number
                        String currentPage = responseJsonObject.getString("page");
                        int totalCount = responseJsonObject.getInt("totalCount"); // Total count of posts is stored in the response sent back
                        int totalPages = (int) Math.ceil(totalCount/10.0); // Calculate total pages the user can go through (total post count / 10 posts per page)

                        // Call the SetTotalPages to set the TotalPagesToDisplay
                        SetTotalPages(totalPages);

                        binding.textViewPaging.setText("Showing page " + currentPage + " out of " + totalPages);
                        mPosts.clear(); // Make sure the list of posts is clean before populating it with the new posts fetched from api

                        // For each API fetched post-object, create a local Post Object and populate mPosts arrayList.
                        for (int i = 0; i < postsArray.length(); i++) {
                            JSONObject postJson = postsArray.getJSONObject(i);
                            Post post = new Post(postJson);
                            mPosts.add(post);
                        }

                        // TODO: Notify adapter
                        getActivity().runOnUiThread(new Runnable() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void run() {
                                postsAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    // set TotalCount to Limit running out of pages to load
    private void SetTotalPages(double pageCount) {
        this.TotalPagesToDisplay = (int) pageCount;
    }

    // Adapter
    class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
        @NonNull
        @Override
        public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            PostRowItemBinding binding = PostRowItemBinding.inflate(getLayoutInflater(), parent, false);
            return new PostsViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {
            Post post = mPosts.get(position);
            holder.setupUI(post);
        }

        @Override
        public int getItemCount() {
            return mPosts.size();
        }

        class PostsViewHolder extends RecyclerView.ViewHolder {
            PostRowItemBinding mBinding;
            Post mPost;
            public PostsViewHolder(PostRowItemBinding binding) {
                super(binding.getRoot());
                mBinding = binding;
            }

            public void setupUI(Post post){

                sharedPreferences = requireActivity().getSharedPreferences("mySharedPref", Context.MODE_PRIVATE); // sharedPreferences declared globally
                String user_id = sharedPreferences.getString("user_id", null);

                mPost = post;
                mBinding.textViewPost.setText(post.getPost_text());
                mBinding.textViewCreatedBy.setText(post.getCreated_by_name());
                mBinding.textViewCreatedAt.setText(post.getCreated_at());

                // Initially set trashcan icon to invisible
                mBinding.imageViewDelete.setVisibility(View.GONE);
                if (mPost.getCreated_by_uid().equals(user_id)) {
                    mBinding.imageViewDelete.setVisibility(View.VISIBLE);
                }

                mBinding.imageViewDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // Alert user and confirm deletion
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Delete Post?")
                                .setMessage("Post ID: " + mPost.getPost_id())
                                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DeletePost(mPost.getPost_id(), token);
                                        Log.d(TAG, "Delete post_id: " + mPost.getPost_id());
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.d(TAG, "Delete Cancelled!");
                                    }
                                });

                        builder.create().show();
                    }
                });
            }
        }
    }

    private void DeletePost(String postId, String token) {

        OkHttpClient client = new OkHttpClient();

        // Build request body
        RequestBody requestBody = new FormBody.Builder()
                .add("post_id", String.valueOf(postId))
                .build();

        // Build request with api url
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/posts/delete")
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

                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: Delete Success " + responseBody);
                    GetPosts(String.valueOf(PageToFetch), token);
                } else {
                    Log.d(TAG, "onResponse: Delete Fail " + responseBody);
                }
            }
        });

    }


    // Interface
    PostsListener mListener;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (PostsListener) context;
    }

    interface PostsListener{
        void logout();
        void createPost();
    }
}