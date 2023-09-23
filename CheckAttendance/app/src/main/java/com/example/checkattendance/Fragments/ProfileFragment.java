package com.example.checkattendance.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.checkattendance.Adapter.InformationAdapter;
import com.example.checkattendance.AddImage;
import com.example.checkattendance.Models.Detail_Profile;
import com.example.checkattendance.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<Detail_Profile> arrayList;
    private String Account_name = "";
    private String Password = "";
    private String Position = "";
    private String Gender = "";
    private String PhoneNumber = "";
    private String EmailAddress = "";
    private ImageButton btn_add_image;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView txt = view.findViewById(R.id.textView2);
        btn_add_image = view.findViewById(R.id.add_image);
        ImageView imageView = view.findViewById(R.id.roundedImageViewpersonal);
        Picasso.get().load(getArguments().getString("Image").trim()).into(imageView);
        txt.setText(getArguments().getString("Name"));
        Account_name = getArguments().getString("AccountName");
        Password = getArguments().getString("Password");
        Position = getArguments().getString("Position");
        Gender = getArguments().getString("Gender");
        PhoneNumber = getArguments().getString("PhoneNumber");
        EmailAddress = getArguments().getString("Email");
        arrayList = new ArrayList<>();
        arrayList.add(new Detail_Profile(R.drawable.accountname, "Account name:", Account_name));
        arrayList.add(new Detail_Profile(R.drawable.padlockpassword, "Password:", Password));
        arrayList.add(new Detail_Profile(R.drawable.role, "Position:", Position));
        arrayList.add(new Detail_Profile(R.drawable.gender, "Gender:", Gender));
        arrayList.add(new Detail_Profile(R.drawable.phonenumber, "Phone Number:", PhoneNumber));
        arrayList.add(new Detail_Profile(R.drawable.email, "Email Address:", EmailAddress));
        InformationAdapter informationAdapter = new InformationAdapter(arrayList);
        informationAdapter.notifyDataSetChanged();
        ListView listView = view.findViewById(R.id.list_item);
        listView.setAdapter(informationAdapter);
        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddImage.class));
            }
        });
        return view;
    }
}