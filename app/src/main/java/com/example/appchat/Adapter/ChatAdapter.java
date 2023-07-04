package com.example.appchat.Adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appchat.Model.ChatMessage;
import com.example.appchat.databinding.ItemContainerReceivedMessageBinding;
import com.example.appchat.databinding.ItemContainerSentmessagerBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
   private final  List<ChatMessage> chatMessage;
   private Bitmap receiverProfileImage;
   private final String senderId;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;

public void setReceiverProfileImage(Bitmap bitmap){
    receiverProfileImage=bitmap;
}
    public ChatAdapter(List<ChatMessage> chatMessage, Bitmap receiverProfileImage, String senderId) {
        this.chatMessage = chatMessage;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainerSentmessagerBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent,
                    false)
            );
        } else {
            return new ReceivedMessageViewHodel(
                    ItemContainerReceivedMessageBinding
                            .inflate(LayoutInflater.from(parent.getContext())
                                    , parent, false)

            );

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessage.get(position));
        } else {
            ((ReceivedMessageViewHodel) holder).setData(chatMessage.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessage.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerSentmessagerBinding binding;

        SentMessageViewHolder(ItemContainerSentmessagerBinding itemContainerSentmessagerBinding) {
            super(itemContainerSentmessagerBinding.getRoot());
            binding = itemContainerSentmessagerBinding;

        }

        void setData(ChatMessage chatMessage) {
            binding.textmessage.setText(chatMessage.message);
            binding.textDatetime.setText((chatMessage.datetime));
        }
    }

    static class ReceivedMessageViewHodel extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHodel(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textmessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.datetime);
            if (receiverProfileImage!=null){
                binding.imageProfile.setImageBitmap(receiverProfileImage);
            }

        }
    }
}
