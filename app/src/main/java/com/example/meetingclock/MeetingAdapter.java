package com.example.meetingclock;

import android.annotation.SuppressLint;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.text.BreakIterator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MeetingAdapter extends ListAdapter<Meeting, MeetingAdapter.MeetingHolder> {
    private OnItemClickListener listener;

    protected MeetingAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Meeting> DIFF_CALLBACK = new DiffUtil.ItemCallback<Meeting>() {
        @Override
        public boolean areItemsTheSame(@NonNull Meeting oldItem, @NonNull Meeting newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Meeting oldItem, @NonNull Meeting newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getAgenda().equals(newItem.getAgenda()) &&
                    oldItem.getStart_datetime() == (newItem.getStart_datetime()) &&
                    oldItem.getEnd_datetime() == (newItem.getEnd_datetime());
        }
    };

    @NonNull
    @Override
    public MeetingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meeting_item, parent, false);
        return new MeetingHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingHolder holder, int position) {
        Meeting currentMeeting = getItem(position);
        holder.textViewTitle.setText(currentMeeting.getTitle());
        holder.textViewAgenda.setText(currentMeeting.getAgenda());

        Calendar day = Calendar.getInstance();
        day.setTimeInMillis(currentMeeting.getStart_datetime()*1000L);

        String month_date = new SimpleDateFormat("MMM dd").format(day.getTimeInMillis());
        String week_date = new SimpleDateFormat("EEE").format(day.getTimeInMillis());

        holder.textViewDate.setText(month_date);
        holder.textViewWeek.setText(week_date);

        int start_hours = (currentMeeting.getStart_datetime()/(60*60) % 24)+3;
        int start_minutes = currentMeeting.getStart_datetime()/(60) % 60;

        holder.textViewStartDatetime.setText(String.format("%02d:%02d", start_hours, start_minutes));

        int end_hours = (currentMeeting.getEnd_datetime()/(60*60) % 24)+3;
        int end_minutes = currentMeeting.getEnd_datetime()/(60) % 60;
        
        holder.textViewEndDatetime.setText(String.format("%02d:%02d", end_hours, end_minutes));

    }

    public Meeting getMeetingAt(int position) {
        return getItem(position);
    }

    class MeetingHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewAgenda;
        private TextView textViewStartDatetime;
        private TextView textViewEndDatetime;
        private TextView textViewDate;
        private TextView textViewWeek;


        public MeetingHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewAgenda = itemView.findViewById(R.id.text_view_agenda);
            textViewStartDatetime = itemView.findViewById(R.id.text_view_start_datetime);
            textViewEndDatetime = itemView.findViewById(R.id.text_view_end_datetime);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewWeek = itemView.findViewById(R.id.text_view_week);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Meeting meeting);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
