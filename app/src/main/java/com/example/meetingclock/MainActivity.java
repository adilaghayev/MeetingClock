package com.example.meetingclock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int ADD_MEETING_REQUEST = 1;
    public static final int EDIT_MEETING_REQUEST = 2;

    private MeetingViewModel meetingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddMeeting = findViewById(R.id.button_add_meeting);
        buttonAddMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditMeeting.class);
                startActivityForResult(intent, ADD_MEETING_REQUEST);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final MeetingAdapter adapter = new MeetingAdapter();
        recyclerView.setAdapter(adapter);

        meetingViewModel = ViewModelProviders.of(this).get(MeetingViewModel.class);
        meetingViewModel.getAllMeetings().observe(this, new Observer<List<Meeting>>() {
            @Override
            public void onChanged(List<Meeting> meetings) {
                adapter.submitList(meetings);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                meetingViewModel.delete(adapter.getMeetingAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Meeting cancelled", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new MeetingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Meeting meeting) {
                Intent intent = new Intent(MainActivity.this, AddEditMeeting.class);
                intent.putExtra(AddEditMeeting.EXTRA_ID, meeting.getId());
                intent.putExtra(AddEditMeeting.EXTRA_TITLE, meeting.getTitle());
                intent.putExtra(AddEditMeeting.EXTRA_AGENDA, meeting.getAgenda());
                intent.putExtra(AddEditMeeting.EXTRA_START_DATETIME, meeting.getStart_datetime());
                intent.putExtra(AddEditMeeting.EXTRA_END_DATETIME, meeting.getEnd_datetime());
                startActivityForResult(intent, EDIT_MEETING_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEETING_REQUEST && resultCode == RESULT_OK) {

            String title = data.getStringExtra(AddEditMeeting.EXTRA_TITLE);
            String agenda = data.getStringExtra(AddEditMeeting.EXTRA_AGENDA);
            String host_name = "User";
            String attendees = "Users";
            int start_datetime = data.getIntExtra(AddEditMeeting.EXTRA_START_DATETIME, 1);
            int end_datetime = data.getIntExtra(AddEditMeeting.EXTRA_END_DATETIME, 1);
            int duration = end_datetime - start_datetime;

            Meeting meeting = new Meeting(title, agenda, host_name, attendees, start_datetime, end_datetime, duration);
            meetingViewModel.insert(meeting);

            Toast.makeText(this, "New meeting added", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_MEETING_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditMeeting.EXTRA_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "Meeting cannot be updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String title = data.getStringExtra(AddEditMeeting.EXTRA_TITLE);
            String agenda = data.getStringExtra(AddEditMeeting.EXTRA_AGENDA);
            String host_name = "User";
            String attendees = "Users";
            int start_datetime = data.getIntExtra(AddEditMeeting.EXTRA_START_DATETIME, 1);
            int end_datetime = data.getIntExtra(AddEditMeeting.EXTRA_END_DATETIME, 1);
            int duration = end_datetime - start_datetime;

            Meeting meeting = new Meeting(title, agenda, host_name, attendees, start_datetime, end_datetime, duration);
            meeting.setId(id);
            meetingViewModel.update(meeting);

            Toast.makeText(this, "Meeting updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Discarded", Toast.LENGTH_SHORT).show();
        }

    }
}