package com.example.examchat;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
    private static final String TAG = "MessageAdapter"; // Добавьте логирование
    private List<Message> messages;

    public void updateMessages(List<Message> newMessages) {
        Log.d(TAG, "Обновление адаптера, новых сообщений: " + (newMessages != null ? newMessages.size() : 0));

        if (messages == null) {
            messages = new ArrayList<>();
        }

        messages.clear();
        if (newMessages != null) {
            messages.addAll(newMessages);
        }

        // Убедитесь, что вызываете notifyDataSetChanged в UI потоке
        runOnUiThreadIfNeeded(() -> notifyDataSetChanged());
    }

    private void runOnUiThreadIfNeeded(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            new Handler(Looper.getMainLooper()).post(action);
        }
    }
    public MessageAdapter(List<Message> messages){
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.tvOwner.setText(message.getOwner());
        holder.tvText.setText(message.getText());
        holder.tvTime.setText(message.getTime());
    }

    @Override
    public int getItemCount(){
        return messages.size();
    }

    //public void updateMessages(List<Message> newMessage){
      //  messages.clear();
      //  messages.addAll(newMessage);
      //  notifyDataSetChanged();
    //}

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvOwner, tvText, tvTime;

        public MessageViewHolder(@NonNull View itemView){
            super(itemView);
            tvOwner = itemView.findViewById(R.id.tvOwner);
            tvText = itemView.findViewById(R.id.tvText);
            tvTime = itemView.findViewById(R.id.tvTime);
        }
    }
}
