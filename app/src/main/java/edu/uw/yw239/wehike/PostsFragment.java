package edu.uw.yw239.wehike;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {
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
        postsLinearManager = new LinearLayoutManager(this.getActivity());
        postsRecyclerView.setLayoutManager(postsLinearManager);

        assert postsRecyclerView != null;
        setupPostsRecyclerView(postsRecyclerView);
        return view;
    }

    private void setupPostsRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new PostsRecyclerViewAdapter(postsList));
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
            //holder.title.setText(mValues.get(position).name);
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
            public ImageView pic;
            public View mView;
            public Post mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.post_name);
                pic = (ImageView) view.findViewById(R.id.post_image);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + title.getText() + "'";
            }
        }
    }

    private void addPostClickAction() {
        //CreatePostActivity
    }

}
