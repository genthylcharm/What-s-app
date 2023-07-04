package com.example.appchat.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.appchat.Adapter.UsersAdapter;
import com.example.appchat.Listenner.UserListenner;
import com.example.appchat.Model.User;
import com.example.appchat.Utilites.Constans;
import com.example.appchat.Utilites.PreferenceManager;
import com.example.appchat.databinding.ActivityUsersBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseAct implements UserListenner{
    private ActivityUsersBinding binding;
    private PreferenceManager preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();
        getUsers();
    }

    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
    }

    // lay nguoi dung tu firebase
    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constans.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currenUserId = preferenceManager.getString(Constans.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currenUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.name = queryDocumentSnapshot.getString(Constans.KEY_NAME);
                            user.email = queryDocumentSnapshot.getString(Constans.KEY_EMAIL);
                            user.image = queryDocumentSnapshot.getString(Constans.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constans.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(users,this );
                            binding.userRecyclerView.setAdapter(usersAdapter);
                            binding.userRecyclerView.setVisibility(View.VISIBLE);
                            binding.userRecyclerView.setPadding(4, 4, 4, 4);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }
// thong bao loi
    private void showErrorMessage() {
        binding.textErroMessage.setText(String.format("%s", "No user available"));
        binding.textErroMessage.setVisibility(View.VISIBLE);
    }

   //  loading progressbar
    private void loading(Boolean isloading) {
        if (isloading) {
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
   }

    @Override
    public void onUserClickid(User user) {

    }

    @Override
    public void onUserClicked(User user) {
        Intent intent= new Intent(getApplicationContext(),ChatActivity.class);
        intent.putExtra(Constans.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}
