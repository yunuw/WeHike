package edu.uw.yw239.wehike.trails;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.uw.yw239.wehike.MainActivity;
import edu.uw.yw239.wehike.R;
import edu.uw.yw239.wehike.common.LocationManager;
import edu.uw.yw239.wehike.common.RequestSingleton;

import static android.R.attr.description;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TrailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TrailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TrailsFragment extends Fragment {
    public static final String Trails_Fragment_Tag = "Trails_Fragment_Tag";
    private static final String TAG = "TrailsFrag";

    private Location curLocation;
    private RecyclerView trailsRecyclerView;
    private LinearLayoutManager trailsLinearManager;
    private TrailsRecyclerViewAdapter trailsRecyclerViewAdapter;
    private List<Trail> trailsList;

    private Button searchButton;
    private EditText searchEditText;

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
     * @return A new instance of fragment TrailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TrailsFragment newInstance(String param1, String param2) {
        TrailsFragment fragment = new TrailsFragment();
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
//            fetchTrails(mQuery,45.1,-122.15);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        this.getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trails, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.maps);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                view.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
            }
        });

        searchButton = (Button)view.findViewById(R.id.searchButton);
        searchEditText = (EditText)view.findViewById(R.id.seachText);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText = (EditText)getActivity().findViewById(R.id.seachText);
                String q = searchEditText.getText().toString();
                Log.v("what is the q?", q);
                fetchTrails(q, 0.0,0.0);
            }
        });


        trailsList = new ArrayList<Trail>();
        trailsRecyclerViewAdapter = new TrailsRecyclerViewAdapter(trailsList);

        trailsRecyclerView = (RecyclerView)view.findViewById(R.id.trails_list);
        trailsLinearManager = new LinearLayoutManager(this.getActivity());
        trailsRecyclerView.setLayoutManager(trailsLinearManager);


        assert trailsRecyclerView != null;

        trailsRecyclerView.setAdapter(trailsRecyclerViewAdapter);

//        setupTrailsRecyclerView(trailsRecyclerView);
        curLocation = LocationManager.getInstance().getLocation();

        fetchTrails("",46.0,-122.15);
        return view;

    }

    public void fetchTrails(String q, Double lat, Double lon){

        //contruct request url
        String http = "";
        if (q.equals("")){
            http = "https://trailapi-trailapi.p.mashape.com/?lat="+lat+"&limit=25&lon="+lon+"&radius=25";
        } else {
            http = "https://trailapi-trailapi.p.mashape.com/?q[city_cont]="+q+"&radius=25";
        }

        Log.v("what is the http?", http);

        Request mRequest = new JsonObjectRequest(Request.Method.GET, http, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v(TAG, response.toString());

                        List<Trail> trails = Trail.parseTrailAPI(response);
                        Log.v(TAG, trails.toString());
//                        mAdapter = new NewsRecyclerViewAdapter(newsList);
//                        ((RecyclerView) recyclerView).setAdapter(mAdapter);
                        trailsList.clear();
                        trailsList.addAll(trails);
                        trailsRecyclerViewAdapter.notifyDataSetChanged();

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG,error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Mashape-Key", "TzMlsrtBxvmshPb8Q6GChK9MIf0mp1YHXeAjsnVxykXTSr482e");
                headers.put("Accept", "text/plain");
                return headers;
            }
        };
        RequestSingleton.getInstance(getContext()).add(mRequest);
    }



    private void setupTrailsRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new TrailsRecyclerViewAdapter(trailsList));
    }


    // construct Trails RecyclerView Adapter
    public class TrailsRecyclerViewAdapter
            extends RecyclerView.Adapter<TrailsRecyclerViewAdapter.ViewHolder> {

        private Context context;
        private final List<Trail> mValues;
        private LayoutInflater mInflater;
        private String[] test = new String[0];


        public TrailsRecyclerViewAdapter(List<Trail> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.trail_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.title.setText(mValues.get(position).name);
            if (!mValues.get(position).picUrl.equals("null")){
                Log.v("not null!",mValues.get(position).picUrl);
                holder.pic.setImageUrl(mValues.get(position).picUrl, RequestSingleton.getInstance(getContext()).getImageLoader());
            } else {
                Log.v("is null!",mValues.get(position).picUrl);
                holder.pic.setDefaultImageResId(R.drawable.no_image_available);
            }


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), TrailsDetailActivity.class);
                    intent.putExtra("trails details",holder.mItem);
                    startActivity(intent);
                    Log.v(TAG, "Trail Item clicked");
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
            public NetworkImageView pic;
            public View mView;
            public Trail mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                title = (TextView) view.findViewById(R.id.trailName);
                pic = (NetworkImageView) view.findViewById(R.id.trailPic);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + title.getText() + "'";
            }
        }
    }

}
