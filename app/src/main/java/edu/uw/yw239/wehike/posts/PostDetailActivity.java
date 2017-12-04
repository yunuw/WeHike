package edu.uw.yw239.wehike.posts;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.AccountInfo;
import edu.uw.yw239.wehike.common.LocationManager;
import edu.uw.yw239.wehike.common.MyApplication;
import edu.uw.yw239.wehike.common.RequestSingleton;
import edu.uw.yw239.wehike.common.StorageManager;
import edu.uw.yw239.wehike.signin.SignInActivity;
import edu.uw.yw239.wehike.signin.SignUpDiaglog;

/**
 * Created by Nan on 12/3/17.
 */

public class PostDetailActivity extends AppCompatActivity{

    private RecyclerView commentsRecyclerView;
    private LinearLayoutManager commentsLinearManager;
    private CommentsRecyclerViewAdapter commentsRecyclerViewAdapter;
    private List<Comment> commentsList;
    private EditText commentText;

    public static final String POST_ID_KEY = "ARG_PARAM_KEY";
    int postId = -1;
    String imageUrl = " ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_detail);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.commentsRecyclerView);
        assert recyclerView != null;

        commentsList = new ArrayList<Comment>();
        commentsRecyclerViewAdapter = new CommentsRecyclerViewAdapter(commentsList);
        recyclerView.setAdapter(commentsRecyclerViewAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Intent intent = getIntent();
        postId = intent.getIntExtra(POST_ID_KEY, -1);
        commentText = (EditText) findViewById(R.id.commentEditText);

        Button btn = (Button) findViewById(R.id.button_send_comment);
        btn.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm =
                        (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }

                attemptCreateComment();
            }
        });

    }

    public void onStart() {
        this.getPost();
        super.onStart();
    }

    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd' 'HH:mm");

    private void getPost() {
        final Resources resources = this.getResources();
        final String backendPrefix = resources.getString(R.string.backend_prefix);

        String urlString = String.format("%s/posts/get?postId=%d&includeComment=true", backendPrefix, postId);
        Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)  {
                        try {
                            boolean success = response.getBoolean("success");

                            if (success) {
                                commentsList.clear();
                                JSONArray jsonComments = response.getJSONArray("comments");
                                JSONObject jsonPostObject = response.getJSONObject("postInfo");
                                NetworkImageView imageView = (NetworkImageView) findViewById(R.id.post_image);
                                imageUrl = jsonPostObject.getString("imageUrl");
                                imageView.setImageUrl(imageUrl, RequestSingleton.getInstance(getApplicationContext()).getImageLoader());

                                for (int i = 0; i < jsonComments.length(); i++) {
                                    JSONObject jsonPost = jsonComments.getJSONObject(i);

                                    Comment comment = new Comment();
                                    comment.commentId = jsonPost.getInt("commentId");
                                    comment.userName = jsonPost.getString("userName");
                                    comment.commentText = jsonPost.getString("content");
                                    Long postDateLong = jsonPost.getLong("timestamp");
                                    comment.timestamp = formatter.format(postDateLong);

                                    commentsList.add(comment);
                                }
                                commentsRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                String msg = response.getString("message");
                            }
                        } catch (JSONException e) {
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errMsg = resources.getString(R.string.error_create_post_fail);
                        if (error.networkResponse != null) {
                            errMsg = errMsg + "\n" + "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data);
                        }
                    }
                }
        );

        RequestSingleton.getInstance(this.getApplicationContext()).add(request);
    }

    public class CommentsRecyclerViewAdapter
            extends RecyclerView.Adapter<CommentsRecyclerViewAdapter.ViewHolder> {

        private Context context;
        private final List<Comment> mValues;
        private LayoutInflater mInflater;
        private String[] test = new String[0];


        public CommentsRecyclerViewAdapter(List<Comment> items) {
            mValues = items;
        }

        @Override
        public CommentsRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comment_list_item, parent, false);
            return new CommentsRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Comment item = mValues.get(position);
            holder.mItem = item;
            holder.commentUserName.setText(mValues.get(position).userName+": ");
            holder.commentText.setText(mValues.get(position).commentText);
            holder.commentDate.setText(mValues.get(position).timestamp);
        }

        @Override
        public int getItemCount() {
            if (mValues != null){
                return mValues.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView commentUserName;
            public TextView commentText;
            public TextView commentDate;
            public View mView;
            public Comment mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                commentUserName = (TextView) view.findViewById(R.id.comment_user_name);
                commentText = (TextView) view.findViewById(R.id.comment_text);
                commentDate = (TextView) view.findViewById(R.id.comment_date);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + commentUserName.getText() + "'";
            }
        }

    }

    protected void attemptCreateComment() {
        // Reset errors.
        commentText.setError(null);

        final String text = commentText.getText().toString().trim();

        View focusView = null;
        boolean cancel = false;


        if (text == null || text.equals("")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.warning_empty_description);
            builder.setPositiveButton(R.string.dialog_ok_button, null);
            builder.show();
            focusView = commentText;
            cancel = true;
        }

        if (!cancel) {
            // Hide the keyboard
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            createComment(text);

        } else if (focusView != null) {
            focusView.requestFocus();
        }
    }

    private void createComment(String content) {
        try {
            String userName = AccountInfo.getCurrentUserName();
            final Resources resources = this.getResources();
            final String backendPrefix = resources.getString(R.string.backend_prefix);

            // call API and register new user
            String urlString = String.format("%s/post/comments/create?postId=%d&userName=%s&content=%s", backendPrefix, postId, userName, content);

            Request request = new JsonObjectRequest(Request.Method.POST, urlString, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response)  {
                            try {
                                boolean success = response.getBoolean("success");

                                if (success) {
                                    Toast.makeText(PostDetailActivity.this, "Comment created", Toast.LENGTH_LONG).show();
                                    setResult(RESULT_OK);
                                    PostDetailActivity.this.finish();
                                } else {
                                    String msg = response.getString("message");
                                    Toast.makeText(PostDetailActivity.this, msg, Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                Toast.makeText(PostDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String errMsg = resources.getString(R.string.error_create_post_fail);
                            if (error.networkResponse != null) {
                                errMsg = errMsg + "\n" + "Status code: " + error.networkResponse.statusCode + "\n" + new String(error.networkResponse.data);
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), errMsg, Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }
            );

            RequestSingleton.getInstance(this).add(request);
        }
        catch (Exception ex) {
            Toast.makeText(this, "Failed to create comment: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        };
    }




}
