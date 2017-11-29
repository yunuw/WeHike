package edu.uw.yw239.wehike;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public ContactsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactsFragment newInstance(String param1, String param2) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private ContactsInfoAdapter contactsInfoAdapter;
    private ArrayList<ContactsInfo> arrayOfContactsInfo;
    final static String CONTACTS_INFO_KEY = "contacts_info_key";

    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // Construct the data source
        arrayOfContactsInfo = new ArrayList<ContactsInfo>();

        // TODO: 11/28/17 replace the test data with real data
        // create data for test
        for(int i = 1; i < 10; i++){
            arrayOfContactsInfo.add(new ContactsInfo("Contact" + i));
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Create the adapter to convert the array to views
        contactsInfoAdapter = new ContactsInfoAdapter(getActivity(), arrayOfContactsInfo);

        // Attach the adapter to a ListView
        AdapterView listView = (AdapterView) rootView.getRootView().findViewById(R.id.lv_contacts);
        listView.setAdapter(contactsInfoAdapter);

        return rootView;
    }

    public class ContactsInfoAdapter extends ArrayAdapter<ContactsInfo> {

        // View lookup cache
        private class ViewHolder {
            TextView name;
        }

        public ContactsInfoAdapter(Context context, ArrayList<ContactsInfo> users) {
            super(context, R.layout.item_contact_info, users);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // Get the data item for this position
            final ContactsInfo contactsInfo = getItem(position);

            // Check if an existing view is being reused, otherwise inflate the view
            ViewHolder viewHolder; // view lookup cache stored in tag

            if (convertView == null) {

                // If there's no view to re-use, inflate a brand new view for row
                viewHolder = new ViewHolder();
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(R.layout.item_contact_info, parent, false);
                viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);

                // Cache the viewHolder object inside the fresh view
                convertView.setTag(viewHolder);
            } else {

                // View is being recycled, retrieve the viewHolder object from tag
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Populate the data from the data object via the viewHolder object into the template view
            viewHolder.name.setText(contactsInfo.name);

            // Set up the listener for items on the listView
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(getActivity(), ConversationActivity.class);
                    intent.putExtra(CONTACTS_INFO_KEY, contactsInfo.name);
                    startActivity(intent);
                }
            });

            // Return the completed view to render on screen
            return convertView;
        }
    }

}
