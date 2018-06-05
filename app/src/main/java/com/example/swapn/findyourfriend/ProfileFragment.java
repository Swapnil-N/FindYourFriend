package com.example.swapn.findyourfriend;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    Button logOutButton;
    ImageView profilePic;
    TextView usernameTextView;
    Button editProfileButton;
    ProgressBar profilePicProgressBarFrag;

    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, null);

        logOutButton = view.findViewById(R.id.logOutButton);
        profilePic = view.findViewById(R.id.profilePic);
        usernameTextView = view.findViewById(R.id.usernameTextView);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        profilePicProgressBarFrag = view.findViewById(R.id.profilePicProgressBarFrag);

        profilePicProgressBarFrag.setVisibility(View.VISIBLE);

        mAuth = FirebaseAuth.getInstance();

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SetYourProfileActivity.class));
                getActivity().finish();
            }
        });

        loadUserInfo();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    private void loadUserInfo() {

        FirebaseUser user = mAuth.getCurrentUser();

        profilePicProgressBarFrag.setVisibility(View.VISIBLE);
        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this).load(user.getPhotoUrl().toString()).into(profilePic);
                profilePicProgressBarFrag.setVisibility(View.GONE);
            }
            if (user.getDisplayName() != null) {
                usernameTextView.setText(user.getDisplayName());
            }
        }
    }
}
