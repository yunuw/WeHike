package edu.uw.yw239.wehike.posts;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.RequestSingleton;

/**
 * Created by Nan on 12/3/17.
 */

public class PostDetailActivity extends AppCompatActivity{

    private RecyclerView commentsRecyclerView;
    private LinearLayoutManager commentsLinearManager;
    private CommentsRecyclerViewAdapter commentsRecyclerViewAdapter;
    private List<Comment> commentsList;


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

    }

    public void onStart() {
        this.getComments();
        super.onStart();
    }

    SimpleDateFormat formatter = new SimpleDateFormat("MM-dd' 'HH:mm");

    private void getComments() {
        final Resources resources = this.getResources();
        final String backendPrefix = resources.getString(R.string.backend_prefix);

        String urlString = String.format("%s/posts/list", backendPrefix);
        Request request = new JsonObjectRequest(Request.Method.GET, urlString, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response)  {
                        try {
                            boolean success = response.getBoolean("success");

                            if (success) {
                                commentsList.clear();
                                JSONArray jsonPosts = response.getJSONArray("posts");
                                for (int i = 0; i < jsonPosts.length(); i++) {
                                    JSONObject jsonPost = jsonPosts.getJSONObject(i);

                                    Comment comment = new Comment();
                                    comment.commentId = jsonPost.getInt("postId");
                                    comment.userName = jsonPost.getString("userName");
                                    comment.commentText = jsonPost.getString("description");
                                    comment.imageUrl = jsonPost.getString("imageUrl");
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
            holder.pic.setImageUrl(mValues.get(position).imageUrl, RequestSingleton.getInstance(getApplicationContext()).getImageLoader());
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
            public NetworkImageView pic;
            public View mView;
            public Comment mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                commentUserName = (TextView) view.findViewById(R.id.comment_user_name);
                commentText = (TextView) view.findViewById(R.id.comment_text);
                commentDate = (TextView) view.findViewById(R.id.comment_date);
                pic = (NetworkImageView) view.findViewById(R.id.post_image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + commentUserName.getText() + "'";
            }
        }
    }


}
