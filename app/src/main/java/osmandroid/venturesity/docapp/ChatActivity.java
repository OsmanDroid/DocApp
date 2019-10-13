package osmandroid.venturesity.docapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import spencerstudios.com.bungeelib.Bungee;

public class ChatActivity extends AppCompatActivity {


    private static final String TAG = "TAG";
    RecyclerView recyclerView;
    EditText editText;
    ImageView sendButton;

    FirebaseAuth mAuth;
    DatabaseReference ref;

    CustomAdapter adapter;
    List<ChatModel> chatModelList;

    MyProgressDialog myProgressDialog;

    String patientUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = findViewById(R.id.recyclerView);
        editText = findViewById(R.id.editText);
        sendButton = findViewById(R.id.send_btn);

        myProgressDialog = new MyProgressDialog(this);
        myProgressDialog.showPDiialog();

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        mAuth = FirebaseAuth.getInstance();

        chatModelList = new ArrayList<>();

        adapter = new CustomAdapter(this, chatModelList, Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        ref = FirebaseDatabase.getInstance().getReference().child("Doctors").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());

        ref.child("patientUID").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                patientUID = dataSnapshot.getValue(String.class);
                if(patientUID!=null)loadChat();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





        sendButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                String message = editText.getText().toString().trim();

                if (!message.equals("")) {

                    ChatModel chatMessage = new ChatModel(message, "doctor");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(patientUID).child("liveD").push().setValue(chatMessage);


                    editText.setText("");




                }

            }
        });

        recyclerView.setAdapter(adapter);
    }



                @Override
                public void onBackPressed () {
                    super.onBackPressed();
                    Bungee.fade(this);
                }

                @Override
                public void onStart () {
                    super.onStart();
                    // Check if user is signed in (non-null) and update UI accordingly.
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                }

                private void updateUI (FirebaseUser user){
                    if (user == null) {
                        Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }




                void loadChat()
                {
                    FirebaseDatabase.getInstance().getReference().child("Users").child(patientUID).child("liveD").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            ChatModel model = dataSnapshot.getValue(ChatModel.class);
                            Log.d(TAG, "onChildAdded: " + model.msgUser);
                            Log.d(TAG, "onChildAdded: " + model.msgText);

                            chatModelList.add(model);
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(chatModelList.size() - 1);

                            myProgressDialog.dismissPDialog();
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    FirebaseDatabase.getInstance().getReference().child("Users").child(patientUID).child("liveD").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.getValue() == null) {
                                // The child doesn't exist
                                myProgressDialog.dismissPDialog();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
    }






