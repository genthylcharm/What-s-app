package com.example.appchat.View;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.appchat.Listenner.ConversionListener;
import com.example.appchat.Model.ChatMessage;
import com.example.appchat.Model.RecentConversationAdapter;
import com.example.appchat.Model.User;
import com.example.appchat.Utilites.Constans;
import com.example.appchat.Utilites.PreferenceManager;
import com.example.appchat.databinding.ActivityHomeBinding;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends BaseAct implements ConversionListener {
    private ActivityHomeBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversation;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        init();
        loadUserDetails();
        getToken();
        setListeners();
        listenConversation();
    }

    private void init() {
        conversation = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversation, this);
        database = FirebaseFirestore.getInstance();
    }

    private void setListeners() {
        binding.imageSignOut.setOnClickListener(v -> SignOut());
        binding.fabNewchat.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constans.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constans.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversation() {
        database.collection(Constans.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constans.KEY_SENDER_ID, preferenceManager.getString(Constans.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constans.KEY_COLLECTION_CONVERSATION)
                .whereEqualTo(Constans.KEY_SENDER_ID, preferenceManager.getString(Constans.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderId = documentChange.getDocument().getString(Constans.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constans.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (preferenceManager.getString(Constans.KEY_USER_ID).equals(senderId)) {
                        chatMessage.ConversionImage = documentChange.getDocument().getString(Constans.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constans.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constans.KEY_RECEIVER_ID);
                    } else {
                        chatMessage.ConversionImage = documentChange.getDocument().getString(Constans.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constans.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constans.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constans.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constans.KEY_TIMESTAMP);
                    conversation.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversation.size(); i++) {
                        String senderId = documentChange.getDocument().getString(Constans.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constans.KEY_RECEIVER_ID);
                        if (conversation.get(i).senderId.equals(senderId) && conversation.get(i).receiverId.equals(receiverId)) {
                            conversation.get(i).message = documentChange.getDocument().getString(Constans.KEY_LAST_MESSAGE);
                            conversation.get(i).dateObject = documentChange.getDocument().getDate(Constans.KEY_TIMESTAMP);
                            break;
                        }
                    }

                }
            }
            Collections.sort(conversation, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationAdapter.notifyDataSetChanged();
            binding.conversationRecyclerView.smoothScrollToPosition(0);
            binding.conversationRecyclerView.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.GONE);
        }
    };

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updatetoken);
    }

    private void updatetoken(String token) {
        preferenceManager.putString(Constans.KEY_FCM_TOKEN, token);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constans.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constans.KEY_USER_ID)
        );
        documentReference.update(Constans.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    // sign out
    private void SignOut() {
        showToast("Sign out...");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constans.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constans.KEY_USER_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constans.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        showToast("Unable to sign out"));
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constans.KEY_USER, user);
        startActivity(intent);
    }
}
