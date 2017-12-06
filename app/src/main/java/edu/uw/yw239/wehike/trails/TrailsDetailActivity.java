package edu.uw.yw239.wehike.trails;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.RequestSingleton;
import edu.uw.yw239.wehike.posts.CreatePostActivity;
import edu.uw.yw239.wehike.posts.PostsFragment;

import static edu.uw.yw239.wehike.common.MyApplication.getContext;

/**
 * Created by wangchen on 12/3/17.
 */

public class TrailsDetailActivity extends AppCompatActivity {
    private static final String TAG = "TrailsDetailActivity";
    public final static String BACK_TO_TRAILS_KEY = "back to trails key";
    public final static int BACK_TO_TRAILS_VALUE = 1;

    private Trail theTrail;
    private RecyclerView actRecyclerView;
    private LinearLayoutManager actLinearManager;
    private ActRecyclerViewAdapter actRecyclerViewAdapter;
    private List<TrailActivity> trailActivities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trails_details);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        trailActivities = new ArrayList<TrailActivity>();
        actRecyclerViewAdapter = new ActRecyclerViewAdapter(trailActivities);

        actRecyclerView = (RecyclerView)findViewById(R.id.trails_details_list);
        actLinearManager = new LinearLayoutManager(this);
        actRecyclerView.setLayoutManager(actLinearManager);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        Log.d(TAG, intent == null ? "Intent is null" : "Intent is not null");
        Log.d(TAG, bundle == null ? "Bundle is null" : "Bundle is not null");
        theTrail = bundle.getParcelable("trails details");
        for (int i = 0; i<theTrail.actNames.size(); i++){
            TrailActivity trailActivity = new TrailActivity();
            trailActivity.actName = theTrail.actNames.get(i);
            trailActivity.actPicUrl = theTrail.actPicUrls.get(i);
            trailActivity.actRating = "Rating: "+ theTrail.actRatings.get(i);
            trailActivity.actDesc = theTrail.actDescriptions.get(i);
            trailActivities.add(trailActivity);
        }


        assert actRecyclerView != null;

        setupTrailsRecyclerView(actRecyclerView);

    }

    private void setupTrailsRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new ActRecyclerViewAdapter(trailActivities));
    }

    public class ActRecyclerViewAdapter
            extends RecyclerView.Adapter<ActRecyclerViewAdapter.ViewHolder> {

        private Context context;
        private List<TrailActivity> mValues;
        private LayoutInflater mInflater;
        private String[] test = new String[0];


        public ActRecyclerViewAdapter(List<TrailActivity> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trails_details_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.detailsTitle.setText(mValues.get(position).actName);
            if (!mValues.get(position).actPicUrl.equals("null")){
                Log.v("not null!",mValues.get(position).actPicUrl);
                holder.detailsPic.setImageUrl(mValues.get(position).actPicUrl, RequestSingleton.getInstance(getContext()).getImageLoader());
            } else {
                Log.v("is null!",mValues.get(position).actPicUrl);
                holder.detailsPic.setDefaultImageResId(R.drawable.no_image_available);
            }

            if (mValues.get(position).actRating.equals("Rating: 0.0")){
                holder.detailsRating.setText("No ratings yet!");
            } else {
                holder.detailsRating.setText(mValues.get(position).actRating);
            }
            holder.description.setText("Description:");
            holder.detailsDesc.setText(mValues.get(position).actDesc);


//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(getActivity(), TrailsDetailActivity.class);
//                    intent.putExtra("trails details",holder.mItem);
//                    startActivity(intent);
//                    Log.v(TAG, "Trail Item clicked");
//                }
//            });
        }

        @Override
        public int getItemCount() {
            if (mValues != null){
                return mValues.size();
            }
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView detailsTitle;
            public NetworkImageView detailsPic;
            public TextView detailsRating;
            public TextView description;
            public TextView detailsDesc;
            public View mView;
            public TrailActivity mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                detailsTitle = (TextView) view.findViewById(R.id.actName);
                detailsPic = (NetworkImageView) view.findViewById(R.id.actPic);
                detailsRating = (TextView) view.findViewById(R.id.actRating);
                detailsDesc = (TextView) view.findViewById(R.id.actDesc);
                description = (TextView) view.findViewById(R.id.description);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + detailsTitle.getText() + "'";
            }
        }
    }

    private void goBack(){
        Intent intent = new Intent(TrailsDetailActivity.this, MainActivity.class);

        intent.putExtra(MainActivity.FRAGMENT_TO_SELECT_KEY, TrailsFragment.Trails_Fragment_Tag);
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        goBack();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }
}
