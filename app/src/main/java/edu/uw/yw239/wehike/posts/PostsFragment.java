package edu.uw.yw239.wehike.posts;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import static edu.uw.yw239.wehike.posts.PostDetailActivity.POST_ID_KEY;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {
    public static final String Posts_Fragment_Tag = "Posts_Fragment_Tag";

    private static final String TAG = "PostsFrag";

    private RecyclerView postsRecyclerView;
    private LinearLayoutManager postsLinearManager;
    private PostsRecyclerViewAdapter postsRecyclerViewAdapter;
    private List<Post> postsList;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String QUERY = "query";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mQuery;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFragment newInstance(String param1, String param2) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putString(QUERY, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuery = getArguments().getString(QUERY);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        FloatingActionButton mFab = (FloatingActionButton) view.findViewById(R.id.add_new_post_fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // go to the create post activity
                Intent intent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(intent);
            }
        });

        postsList = new ArrayList<Post>();
        postsRecyclerViewAdapter = new PostsRecyclerViewAdapter(postsList);

        postsRecyclerView = (RecyclerView)view.findViewById(R.id.posts_list);
        assert postsRecyclerView != null;

        postsLinearManager = new LinearLayoutManager(this.getActivity());
        postsRecyclerView.setLayoutManager(postsLinearManager);
        postsRecyclerView.setAdapter(postsRecyclerViewAdapter);

        return view;
    }

    public void onStart() {
        this.getPosts();
        super.onStart();
    }

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    private void getPosts() {
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
                                postsList.clear();
                                JSONArray jsonPosts = response.getJSONArray("posts");
                                for (int i = 0; i < jsonPosts.length(); i++) {
                                    JSONObject jsonPost = jsonPosts.getJSONObject(i);

                                    Post post = new Post();
                                    post.postId = jsonPost.getInt("postId");
                                    post.userName = jsonPost.getString("userName");
                                    post.imageUrl = jsonPost.getString("imageUrl");
                                    post.userImageUrl = jsonPost.getString("userPhotoUrl");
                                    post.description = jsonPost.getString("description");
                                    post.longitude = jsonPost.getDouble("longitude");
                                    post.latitude = jsonPost.getDouble("latitude");

                                    Long postDateLong = jsonPost.getLong("timestamp");
                                    post.timestamp = formatter.format(postDateLong);

                                    postsList.add(post);

                                }
                                postsRecyclerViewAdapter.notifyDataSetChanged();
                            } else {
                                String msg = response.getString("message");
                                Toast.makeText(PostsFragment.this.getContext(), msg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Toast.makeText(PostsFragment.this.getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
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

                        Toast.makeText(PostsFragment.this.getContext(), errMsg, Toast.LENGTH_LONG).show();
                    }
                }
        );

        RequestSingleton.getInstance(this.getContext()).add(request);
    }

    public class PostsRecyclerViewAdapter
            extends RecyclerView.Adapter<PostsRecyclerViewAdapter.ViewHolder> {

        private Context context;
        private final List<Post> mValues;
        private LayoutInflater mInflater;
        private String[] test = new String[0];


        public PostsRecyclerViewAdapter(List<Post> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.posts_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.title.setText(mValues.get(position).userName);
            holder.pic.setImageUrl(mValues.get(position).imageUrl, RequestSingleton.getInstance(getContext()).getImageLoader());

            holder.postText.setText(mValues.get(position).description);
            holder.postDate.setText(mValues.get(position).timestamp);

            if (holder.userpic != null) {
                holder.userpic.setImageUrl(mValues.get(position).userImageUrl, RequestSingleton.getInstance(getContext()).getImageLoader());
            } else {
                holder.userpic.setDefaultImageResId(R.mipmap.ic_profile_picture);
            }

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, PostDetailActivity.class);
                    intent.putExtra(POST_ID_KEY, holder.mItem.postId);
                    context.startActivity(intent);
                }

            });

        }

        @Override
        public int getItemCount() {
            if (mValues != null){
                return mValues.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView title;
            public TextView postText;
            public TextView postDate;
            public NetworkImageView pic;
            public NetworkImageView userpic;
            public View mView;
            public Post mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.post_name);
                pic = (NetworkImageView) view.findViewById(R.id.post_image);
                userpic = (NetworkImageView) view.findViewById(R.id.user_image);
                postText = (TextView) view.findViewById(R.id.post_description);
                postDate = (TextView) view.findViewById(R.id.post_date);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + title.getText() + "'";
            }
        }
    }
}
