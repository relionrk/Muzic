package com.rkrelion.muzic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.rkrelion.muzic.R;
import com.google.firebase.auth.FirebaseAuth;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class local extends AppCompatActivity {

    RecyclerView recyclerView ;
    ArrayList<File> mySongs = new ArrayList<>() ;

    MenuInflater inflater ;
    ArrayList<String> items = new ArrayList<>();
    HashMap<String , Integer> map = new HashMap<String, Integer>();
    String userId ;

    myAdapter ad ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        getSupportActionBar().setTitle("Songs");

        recyclerView = findViewById(R.id.recyclerView) ;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid() ;

        databaseHelper dbh = new databaseHelper(this , "PlayList"+userId , null , 1) ;

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE )
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

//                        Toast.makeText(MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT).show();
                        mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                        Collections.sort(mySongs);

                        SharedPreferences sP = getSharedPreferences("Favourites"+userId , MODE_PRIVATE) ;
                        SharedPreferences.Editor ed = sP.edit() ;

                        for (int i=0 ; i<mySongs.size() ; i++) {
                            String s = mySongs.get(i).getName().replace(".mp3","") ;
                            items.add(s);
                            map.put(s , i) ;
                            if ( !sP.contains(s) ) {
                                ed.putBoolean(s, false) ;
                            }

                        }
                        ed.apply();

                        recyclerView.setLayoutManager(new LinearLayoutManager(local.this));
                        ad = new myAdapter(local.this , mySongs , map) ;
                        recyclerView.setAdapter(ad);


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })

                .check();
    }

    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File [] songs = file.listFiles();
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu_songs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case R.id.app_bar_search:
                MenuItem searchItem = item ;
                SearchView searchView = (SearchView) searchItem.getActionView();
                //dbh.close();

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        ad.getFilter().filter(newText);
                        return false;
                    }
                });
                break ;

            case R.id.open_playlist:
                //ArrayList<Integer> indexes = dbh.getItems() ;
                Intent intent = new Intent(this , PlayList.class) ;
                intent.putExtra("songList" , mySongs) ;
                //intent.putExtra("playlistSongs", indexes);
                intent.putExtra("map", map) ;
                startActivity(intent);
                break ;

            case R.id.Favourites:
//                SharedPreferences sP = getSharedPreferences("Favourites" , MODE_PRIVATE) ;
//                ArrayList<Integer> fav = new ArrayList<>() ;
//                for (int i=0 ; i< mySongs.size() ; i++) {
//                    String name = mySongs.get(i).getName().replace(".mp3" , "") ;
//                    if (sP.getBoolean(name , false))
//                        fav.add(i) ;
//                }
//                ad.update(fav);
//                break ;
                Intent intent1 = new Intent(this , myFavourites.class) ;
                intent1.putExtra("songList" , mySongs) ;
                intent1.putExtra("map" , map) ;
                startActivity(intent1);
                break ;

            default:
                return false ;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext() , myProfile.class) ;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) ;
        startActivity(intent);
        super.onBackPressed();
    }
}