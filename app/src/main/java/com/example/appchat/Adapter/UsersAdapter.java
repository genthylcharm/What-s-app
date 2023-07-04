package com.example.appchat.Adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Listenner.UserListenner;
import com.example.appchat.Model.User;
import com.example.appchat.databinding.ItemContainerUserBinding;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHodel> {
    private final List<User> users;
    private final UserListenner userListenner;

    public UsersAdapter(List<User> users, UserListenner userListenner) {

        this.users = users;
        this.userListenner = userListenner;
    }

    @NonNull
    @Override
    public UserViewHodel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent, false
        );
        return new UserViewHodel(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHodel holder, int position) {
        holder.setUserData(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHodel extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;

        UserViewHodel(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(view -> userListenner.onUserClicked(user));
        }
    }

    // xu ly anh hien thi
    private Bitmap getUserImage(String encodeImage) {
        byte[] bytes = Base64.decode(encodeImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
