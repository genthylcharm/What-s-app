package com.example.appchat.View;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.appchat.Utilites.Constans;
import com.example.appchat.Utilites.PreferenceManager;
import com.example.appchat.databinding.ActivitySignInBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager= new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constans.KEY_IS_SIGNED_IN)) {
            Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListteners();
    }

    private void setListteners() {
        binding.textRegister.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
binding.btnSignIn.setOnClickListener(view -> {
    signIn();
});

    }
    private void signIn(){
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(Constans.KEY_COLLECTION_USERS)
                .whereEqualTo(Constans.KEY_EMAIL,binding.inputEmail.getText().toString())
                .whereEqualTo(Constans.KEY_PASSWORD,binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()&&task.getResult()!=null
                   &&task.getResult().getDocuments().size()>0){
                       DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                       preferenceManager.putBoolean(Constans.KEY_IS_SIGNED_IN,true);
                       preferenceManager.putString(Constans.KEY_USER_ID,documentSnapshot.getId());
                       preferenceManager.putString(Constans.KEY_NAME,documentSnapshot.getString(Constans.KEY_NAME));
                       preferenceManager.putString(Constans.KEY_IMAGE,documentSnapshot.getString(Constans.KEY_IMAGE));
                       Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                   }else {
                       loading(false);
                       showToast("Unable to sign in");
                   }
                });

    }
private void loading(Boolean isLoading){
        if (isLoading){
binding.btnSignIn.setVisibility(View.INVISIBLE);
binding.progressbar.setVisibility(View.VISIBLE);
        }else {
            binding.progressbar.setVisibility(View.INVISIBLE);
            binding.btnSignIn.setVisibility(View.VISIBLE);
        }
}
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter valid email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter vailid Email");
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
showToast("enter password");
return false;
        }else {
            return true;
        }
    }

}