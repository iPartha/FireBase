package com.example.ipartha.firebase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by iPartha on 30-03-2017.
 */

public class UserView extends AppCompatActivity{

    TextView textUser;
    CircleImageView imageProfile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        textUser = (TextView)findViewById(R.id.textUserId);
        imageProfile = (CircleImageView)findViewById(R.id.profile_image);
        updateUserProfile();
    }

    public void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            Log.i("User Name", name);
            Log.i("Email", email);

            Log.i("Photo URL", photoUrl.toString());

            textUser.setText(name);

            new ImageDownloadTask(imageProfile).execute(photoUrl.toString());

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
        } else {
            AlertMsg.showToast(LoginActivity.getContext(), "Failed to get user details.");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FirebaseAuth.getInstance().signOut();
        fileList();
    }

    public void onClickSignOut(View v) {

        onBackPressed();

    }
}


