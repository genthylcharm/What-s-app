package com.example.appchat.Listenner;

import com.example.appchat.Model.User;

public interface UserListenner {
    void onUserClickid(User user);

    void onUserClicked(User user);
}
