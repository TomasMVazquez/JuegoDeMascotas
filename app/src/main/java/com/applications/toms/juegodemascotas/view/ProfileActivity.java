package com.applications.toms.juegodemascotas.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.applications.toms.juegodemascotas.R;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    public static final String KEY_TYPE = "type";
    public static final String KEY_USER_ID = "user_id";

    private ImageView ivProfile;
    private TextView tvName;
    private TextView tvDir;
    private TextView tvAboutProfile;
    private TextView tvMyPetsOwner;
    private RecyclerView rvMyPetsOwner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Auth
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        ivProfile = findViewById(R.id.ivProfile);
        tvName = findViewById(R.id.tvName);
        tvDir = findViewById(R.id.tvDir);
        tvAboutProfile = findViewById(R.id.tvAboutProfile);
        tvMyPetsOwner = findViewById(R.id.tvMyPetsOwner);
        rvMyPetsOwner = findViewById(R.id.rvMyPetsOwner);

        //intent
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        String type = bundle.getString(KEY_TYPE);
        String userId = bundle.getString(KEY_USER_ID);

        if (type.equals("1")){
            tvMyPetsOwner.setText(getResources().getString(R.string.my_pets));
            //TODO que pasa si quiere ver el profile de otro usuario
            fetchOwnerProfile(mAuth.getCurrentUser());
        }else {
            tvMyPetsOwner.setText(getResources().getString(R.string.my_owner));
        }

    }

    public void fetchOwnerProfile(FirebaseUser user){
        String photo = user.getPhotoUrl().toString() + "?height=500";
        Glide.with(this).load(photo).into(ivProfile);
//        Glide.with(this).load(user.getPhotoUrl()).into(ivProfile);
        if (user.getDisplayName()!=null) {
            tvName.setText(user.getDisplayName());
        }else {
            tvName.setText(user.getEmail());
        }
//        tvDir.setText("");
//        tvAboutProfile.setText("");
        //TODO Recycler de mascotas/Owner
//        rvMyPetsOwner.setAdapter("");
    }

}
