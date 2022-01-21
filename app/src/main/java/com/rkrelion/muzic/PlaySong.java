package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rkrelion.muzic.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class PlaySong extends AppCompatActivity {
    TextView textView;
    ImageView play, previous, next, shuffle, loop , add , minus , heart;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position , maxCount = 0 ;
    SeekBar seekBar;
    Thread updateSeek;
    HashMap<String,Integer> map = new HashMap<>() ;
    boolean shuffleOn = false , loopOn = false ;
    String lastPlayed = "lastPlayed" , mostPlayed = "mostPlayed" , userId;

//    SharedPreferences sP = this.getSharedPreferences("Favourites" , MODE_PRIVATE) ;
//    SharedPreferences.Editor ed = sP.edit() ;


    FirebaseFirestore fStore ;
    FirebaseAuth fAuth ;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportActionBar().setTitle("Music Player");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        shuffle = findViewById(R.id.shuffle);
        loop = findViewById(R.id.loop);
        heart = findViewById(R.id.heart);
        add = findViewById(R.id.add);
        minus = findViewById(R.id.minus);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong").replace(".mp3" , "") ;
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position", 0);
        map = (HashMap<String, Integer>) intent.getSerializableExtra("map");
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        fStore = FirebaseFirestore.getInstance() ;
        fAuth = FirebaseAuth.getInstance() ;
        userId = fAuth.getCurrentUser().getUid() ;

        databaseHelper dbh = new databaseHelper(this , "PlayList"+userId , null , 1) ;

        SharedPreferences sP = getSharedPreferences("Favourites"+userId , MODE_PRIVATE) ;
        SharedPreferences.Editor ed = sP.edit() ;

        SharedPreferences lP = getSharedPreferences("last"+userId , MODE_PRIVATE) ;
        SharedPreferences.Editor ld = lP.edit() ;

        SharedPreferences mP = getSharedPreferences("most"+userId , MODE_PRIVATE) ;
        SharedPreferences.Editor md = mP.edit();

        SharedPreferences cP = getSharedPreferences("count"+userId , MODE_PRIVATE) ;
        SharedPreferences.Editor cd = cP.edit() ;

        ld.putString(lastPlayed,textContent) ;
        ld.apply();

        int prev = cP.getInt(textContent , 0) ;
        cd.putInt(textContent , prev+1) ;
        cd.apply();

        if (prev + 1 > maxCount) {
            md.putString(mostPlayed , textContent) ;
            maxCount = prev + 1 ;
        }
        md.apply();


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        class myThread extends Thread {
            @Override
            public void run() {
                int currentPosition = 0;
                try{
                    while( currentPosition<=mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(10);
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        myThread updateSeek = new myThread() ;
        updateSeek.start();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    play.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else{
                    play.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();

                if (loopOn) {
                    //don't need to change position
                }
                else if (shuffleOn) {
                    Random rand = new Random() ;
                    position = rand.nextInt((songs.size() - 1) - 0 + 1) + 0;
                } else {
                    if(position!=0){
                        position = position - 1;
                    }
                    else{
                        position = songs.size() - 1;
                    }
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
                myThread updateSeek = new myThread() ;
                updateSeek.start();
                textContent = songs.get(position).getName().replace(".mp3" , "");
                textView.setText(textContent);


                String songName = songs.get(position).getName().replace(".mp3" , "");
                if (sP.getBoolean(songName , false)) {
                    heart.setImageResource(R.drawable.heartfoc);
                } else {
                    heart.setImageResource(R.drawable.heart);
                }

                ld.putString(lastPlayed , songName) ;
                ld.apply();

                int prev = cP.getInt(songName , 0) ;
                cd.putInt(songName , prev+1) ;
                cd.apply();

                if (prev + 1 > maxCount) {
                    md.putString(mostPlayed , songName) ;
                    maxCount = prev + 1 ;
                }
                md.apply();

                heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sP.getBoolean(songName , false)) {
                            heart.setImageResource(R.drawable.heart);
                            ed.putBoolean(songName , false) ;

                            HashMap<String , Object> songMap = new HashMap<>() ;
                            String key = "" ;
                            for (int i=0 ; i<songName.length() ; i++) {
                                char c = songName.charAt(i) ;
                                if (Character.isLetterOrDigit(c))
                                    key += c ;
                            }
                            songMap.put(key , FieldValue.delete()) ;
                            DocumentReference documentReference = fStore.collection("fav").document(userId) ;
                            documentReference.update(songMap) ;

                        } else {
                            heart.setImageResource(R.drawable.heartfoc);
                            ed.putBoolean(songName , true) ;

                            HashMap<String , Object> songMap = new HashMap<>() ;
                            String key = "" ;
                            for (int i=0 ; i<songName.length() ; i++) {
                                char c = songName.charAt(i) ;
                                if (Character.isLetterOrDigit(c))
                                    key += c ;
                            }
                            songMap.put(key , songName) ;
                            DocumentReference documentReference = fStore.collection("fav").document(userId) ;
                            documentReference.update(songMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG" , "Song Added") ;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.d("TAG" , "Failed") ;
                                        }
                                    }) ;
                        }
                        ed.apply();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Integer> items = dbh.getItems() ;

                        int actualIndex = map.get(songName) ;

                        if (items.size() != 0 && items.contains(actualIndex)) {
                            Toast.makeText(PlaySong.this, "This Song is already added to Playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            dbh.addItem(actualIndex) ;
                            Toast.makeText(PlaySong.this, "Song added to Playlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Integer> items = dbh.getItems() ;

                        int actualIndex = map.get(songName) ;
                        if (items.size() == 0 || !items.contains(actualIndex) ) {
                            Toast.makeText(PlaySong.this, "Song is not present in Playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            dbh.removeItem(actualIndex) ;
                            Toast.makeText(PlaySong.this, "Song removed from Playlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        next.performClick();
                    }
                }) ;

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (loopOn) {
                    //don't need to change position
                }
                else if (shuffleOn) {
                    Random rand = new Random() ;
                    position = rand.nextInt((songs.size() - 1) - 0 + 1) + 0;
                } else {
                    if(position != songs.size()-1){
                        position = position + 1;
                    }
                    else{
                        position = 0 ;
                    }
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                seekBar.setProgress(0);
                seekBar.setMax(mediaPlayer.getDuration());
                myThread updateSeek = new myThread() ;
                updateSeek.start();
                textContent = songs.get(position).getName().replace(".mp3" , "");
                textView.setText(textContent);


                String songName = songs.get(position).getName().replace(".mp3" , "");
                if (sP.getBoolean(songName , false)) {
                    heart.setImageResource(R.drawable.heartfoc);
                } else {
                    heart.setImageResource(R.drawable.heart);
                }

                ld.putString(lastPlayed , songName) ;
                ld.apply();

                int prev = cP.getInt(songName , 0) ;
                cd.putInt(songName , prev+1) ;
                cd.apply();

                if (prev + 1 > maxCount) {
                    md.putString(mostPlayed , songName) ;
                    maxCount = prev + 1 ;
                }
                md.apply();

                heart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (sP.getBoolean(songName , false)) {
                            heart.setImageResource(R.drawable.heart);
                            ed.putBoolean(songName , false) ;

                            HashMap<String , Object> songMap = new HashMap<>() ;
                            String key = "" ;
                            for (int i=0 ; i<songName.length() ; i++) {
                                char c = songName.charAt(i) ;
                                if (Character.isLetterOrDigit(c))
                                    key += c ;
                            }
                            songMap.put(key , FieldValue.delete()) ;
                            DocumentReference documentReference = fStore.collection("fav").document(userId) ;
                            documentReference.update(songMap) ;

                        } else {
                            heart.setImageResource(R.drawable.heartfoc);
                            ed.putBoolean(songName , true) ;

                            HashMap<String , Object> songMap = new HashMap<>() ;
                            String key = "" ;
                            for (int i=0 ; i<songName.length() ; i++) {
                                char c = songName.charAt(i) ;
                                if (Character.isLetterOrDigit(c))
                                    key += c ;
                            }
                            songMap.put(key , songName) ;
                            DocumentReference documentReference = fStore.collection("fav").document(userId) ;
                            documentReference.update(songMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG" , "Song Added") ;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.d("TAG" , "Failed") ;
                                        }
                                    }) ;
                        }
                        ed.apply();
                    }
                });

                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Integer> items = dbh.getItems() ;
                        int actualIndex = map.get(songName) ;

                        if (items.size() != 0 && items.contains(actualIndex)) {
                            Toast.makeText(PlaySong.this, "This Song is already added to Playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            dbh.addItem(actualIndex) ;
                            Toast.makeText(PlaySong.this, "Song added to Playlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<Integer> items = dbh.getItems() ;
                        int actualIndex = map.get(songName) ;
                        if (items.size() == 0 || !items.contains(actualIndex) ) {
                            Toast.makeText(PlaySong.this, "Song is not present in Playlist", Toast.LENGTH_SHORT).show();
                        } else {
                            dbh.removeItem(actualIndex) ;
                            Toast.makeText(PlaySong.this, "Song removed from Playlist", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        next.performClick();
                    }
                }) ;
            }
        });

        shuffle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffleOn = !shuffleOn ;


                if (shuffleOn) {
                    shuffle.setImageResource(R.drawable.shuffleon);
                    if (loopOn) {
                        loop.performClick() ;
                    }
                } else {
                    shuffle.setImageResource(R.drawable.shuffle);
                }


            }
        });

        loop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loopOn = !loopOn ;

                if (loopOn) {
                    loop.setImageResource(R.drawable.loopon);
                    if (shuffleOn) {
                        shuffle.performClick() ;
                    }
                } else {
                    loop.setImageResource(R.drawable.loop);
                }
            }
        });

        String songName = songs.get(position).getName().replace(".mp3" , "");

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> items = dbh.getItems() ;

                int actualIndex = map.get(songName) ;

                if (items.size() != 0 && items.contains(actualIndex)) {
                    Toast.makeText(PlaySong.this, "This Song is already added to Playlist", Toast.LENGTH_SHORT).show();
                } else {
                    dbh.addItem(actualIndex) ;
                    Toast.makeText(PlaySong.this, "Song added to Playlist", Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(PlaySong.this, "size of map is " + map.size(), Toast.LENGTH_SHORT).show();
                //dbh.addItem(position) ;
                //Toast.makeText(PlaySong.this, "Song added to Playlist", Toast.LENGTH_SHORT).show();
                //dbh.display();
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Integer> items = dbh.getItems() ;

                int actualIndex = map.get(songName) ;
                if (items.size() == 0 || !items.contains(actualIndex) ) {
                    Toast.makeText(PlaySong.this, "Song is not present in Playlist", Toast.LENGTH_SHORT).show();
                } else {
                    dbh.removeItem(actualIndex) ;
                    Toast.makeText(PlaySong.this, "Song removed from Playlist", Toast.LENGTH_SHORT).show();
                }
            }
        });


        if (sP.getBoolean(songName , false)) {
            heart.setImageResource(R.drawable.heartfoc);
        } else {
            heart.setImageResource(R.drawable.heart);
        }

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                boolean b = sP.getBoolean(songName , false) ;
//                Toast.makeText(PlaySong.this, "this is " + b, Toast.LENGTH_SHORT).show();
//                ed.putBoolean(songName , false) ;
//                ed.apply();

                if (sP.getBoolean(songName , false)) {
                    heart.setImageResource(R.drawable.heart);
                    ed.putBoolean(songName , false) ;

                    HashMap<String , Object> songMap = new HashMap<>() ;
                    String key = "" ;
                    for (int i=0 ; i<songName.length() ; i++) {
                        char c = songName.charAt(i) ;
                        if (Character.isLetterOrDigit(c))
                            key += c ;
                    }
                    songMap.put(key , FieldValue.delete()) ;
                    DocumentReference documentReference = fStore.collection("fav").document(userId) ;
                    documentReference.update(songMap) ;
                } else {
                    heart.setImageResource(R.drawable.heartfoc);
                    ed.putBoolean(songName , true) ;

                    HashMap<String , Object> songMap = new HashMap<>() ;
                    String key = "" ;
                    for (int i=0 ; i<songName.length() ; i++) {
                        char c = songName.charAt(i) ;
                        if (Character.isLetterOrDigit(c))
                            key += c ;
                    }
                    songMap.put(key , songName) ;
                    DocumentReference documentReference = fStore.collection("fav").document(userId) ;
                    documentReference.update(songMap)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG" , "Song Added") ;
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.d("TAG" , "Error: " + e.toString()) ;
                                }
                            }) ;
                }
                ed.apply();
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next.performClick();
            }
        }) ;






    }
}