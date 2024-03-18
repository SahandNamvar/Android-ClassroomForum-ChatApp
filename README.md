# MainActivity Notes

## Overview
- MainActivity serves as the main entry point of the application.
- It handles user authentication, navigation between different fragments, and SharedPreferences management for storing user information and authentication tokens.

## onCreate(Bundle savedInstanceState)
- Method called when the activity is first created.
- Sets the content view to activity_main layout.
- Checks if the user is already authenticated by checking if their token exists in SharedPreferences.
  - If authenticated, replaces the container view with PostsFragment.
  - If not authenticated, replaces the container view with LoginFragment.

## createNewAccount()
- Navigates to SignUpFragment by replacing the container view.

## authCompleted(AuthResponse authResponse)
- Saves user information and authentication token to SharedPreferences after successful authentication.
- Replaces the container view with PostsFragment after authentication.

## gotologin()
- Navigates to LoginFragment by replacing the container view.

## logout()
- Deletes user information and authentication token from SharedPreferences upon logout.
- Navigates to LoginFragment after logout and adds the transaction to the back stack.
- Logs a debug message indicating SharedPreferences deletion.

## createPost()
- Navigates to CreatePostFragment by replacing the container view and adding the transaction to the back stack.

## goBackToPosts()
- Pops the back stack to return to the previous fragment (PostsFragment).

## isUserAuthenticated()
- Checks if the user is authenticated by retrieving token and user information from SharedPreferences.
- Returns true if token and user information are present, indicating the user is authenticated.

 
# LoginFragment Notes

## Overview
- LoginFragment handles user login functionality by sending HTTP requests to the server's /login API endpoint.
- It also includes UI elements for entering email and password, as well as buttons for logging in and creating a new account.

## onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
- Inflates the layout for this fragment using data binding.

## onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
- Sets up click listeners for the login and create new account buttons.
- Sets the title of the activity to the login label.

## Login(String email, String password)
- Makes an asynchronous HTTP POST request to the server's /login API endpoint with the provided email and password.
- Handles the response from the server, parsing JSON data to create an AuthResponse object upon successful login.
- Shows toast messages on the UI thread to inform the user about the success or failure of the login attempt.

## ShowToastOnUIThread(String msg)
- Displays a toast message on the UI thread.

## onAttach(@NonNull Context context)
- Attaches the LoginListener interface to the hosting activity to communicate events back to MainActivity.

## Interface
- Defines the LoginListener interface with methods for creating a new account and completing the authentication process.
 
# SignUpFragment Notes

## Overview
- SignUpFragment facilitates user registration by sending HTTP POST requests to the server's /signup API endpoint.
- It includes UI elements for entering user information such as name, email, and password.

## onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
- Inflates the layout for this fragment using data binding.

## onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
- Sets up click listeners for the cancel and signup buttons.
- Sets the title of the activity to the create account label.

## SingUp(String name, String email, String password)
- Makes an asynchronous HTTP POST request to the server's /signup API endpoint with the provided user information.
- Handles the response from the server, parsing JSON data to create an AuthResponse object upon successful user registration.
- Shows toast messages on the UI thread to inform the user about the success or failure of the registration attempt.

## ShowToastOnUIThread(String msg)
- Displays a toast message on the UI thread.

## onAttach(@NonNull Context context)
- Attaches the SignUpListener interface to the hosting activity to communicate events back to MainActivity.

## Interface
- Defines the SignUpListener interface with methods for navigating to the login screen and completing the authentication process.
 
# PostsFragment Notes

## Overview
- PostsFragment displays a list of posts fetched from the server's /posts API endpoint.
- It allows users to view posts, navigate between pages of posts, create new posts, and delete their own posts.

## onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
- Inflates the layout for this fragment using data binding.

## onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
- Sets up UI components, such as buttons and RecyclerView, and initializes adapter and layout manager.
- Fetches user token from SharedPreferences and updates UI with user's name.
- Sets click listeners for buttons to handle creating new posts, logging out, and navigating between pages of posts.
- Initiates the first request to fetch posts upon fragment creation.

## GetPosts(String pageToFetch, String token)
- Makes an asynchronous HTTP GET request to the server's /posts API endpoint with the page number and user token as parameters (& header for authentication).
- Handles the response from the server, parses JSON data to create Post objects, and updates the UI with the retrieved posts.

## SetTotalPages(double pageCount)
- Calculates and sets the total number of pages of posts to be displayed based on the total post count.

## PostsAdapter
- Manages the RecyclerView for displaying posts.
- Implements onCreateViewHolder(), onBindViewHolder(), and getItemCount() methods for populating and updating the UI with posts.

## PostsViewHolder
- Manages individual items in the RecyclerView.
- Binds post data to UI elements and sets up click listener for deleting posts.

## DeletePost(String postId, String token)
- Makes an asynchronous HTTP POST request to the server's /posts/delete API endpoint with the post ID and user token as parameters.
- Handles the response from the server and refreshes the list of posts if the deletion is successful.

## Interface
- Defines the PostsListener interface with methods for logging out and creating new posts.
 
# CreatePostFragment Notes

## Overview
- CreatePostFragment allows users to create new posts by sending HTTP POST requests to the server's /posts/create API endpoint.
- It includes UI elements for entering the text of the post and buttons for submitting or canceling the post creation.

## onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
- Inflates the layout for this fragment using data binding.

## onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
- Sets up UI components and click listeners for the cancel and submit buttons.
- Sets the title of the activity to the create post label.

## CreatePost(String postText)
- Makes an asynchronous HTTP POST request to the server's /posts/create API endpoint with the post text and user token as parameters (& header for authentication).
- Handles the response from the server, displaying toast messages to inform the user about the success or failure of the post creation.

## ShowToastOnUIThreat(String msg)
- Displays a toast message on the UI thread.

## Interface
- Defines the CreatePostListener interface with a method for navigating back to the posts fragment after post creation.
 
# Post Model Notes

## Overview
- The Post class represents a post object retrieved from the server's API.
- It implements the Serializable interface to allow for serialization and passing between activities/fragments.
- The class contains attributes representing various properties of a post, such as creator name, ID, text, creation timestamp, etc.

## Constructor
- The constructor initializes a Post object using a JSON object received from the server's API response.
- It extracts relevant data from the JSON object and assigns it to the corresponding attributes of the Post object.

## Getter Methods
- Provides getter methods for accessing the attributes of the Post object.

## toString() Method
- Overrides the toString() method to provide a string representation of the Post object for debugging purposes.
 
# AuthResponse Model Notes

## Overview
- The AuthResponse class represents the response received from the server upon successful authentication or login.
- It contains attributes for user ID, user full name, and authentication token.

## Constructors
1. Default Constructor: Initializes an empty AuthResponse object.
2. Parameterized Constructor: Initializes an AuthResponse object using a JSON object received from the server's API response.
   - Extracts user ID, user full name, and token from the JSON object and assigns them to the corresponding attributes of the AuthResponse object.

## Getter and Setter Methods
- Provides getter and setter methods for accessing and modifying the attributes of the AuthResponse object.

## toString() Method
- Overrides the toString() method to provide a string representation of the AuthResponse object for debugging purposes.

**All APIs Tested Using Postman**