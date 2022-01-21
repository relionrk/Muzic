package com.rkrelion.muzic;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rkrelion.muzic.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class myAdapter extends RecyclerView.Adapter<myAdapter.ViewHolder> implements Filterable {

    private ArrayList<File> localDataSet = new ArrayList<>();
    private ArrayList<File> newDataSet = new ArrayList<>() ;
    HashMap<String , Integer> map = new HashMap<String , Integer>();
    Context myContext ;



    //private ArrayList<File> mySongs = new ArrayList<>();
    //private int pos ;
    private Context context ;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView ;
        private LinearLayout linearLayout ;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textView = (TextView) view.findViewById(R.id.userFavSong);
            linearLayout = (LinearLayout) view.findViewById(R.id.linearLayout2) ;
        }

        public TextView getTextView() {
            return textView;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     *  String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public myAdapter(Context context ,  ArrayList<File> mySongs , HashMap<String , Integer> map) {

        localDataSet = mySongs ;
        this.context = context ;
        this.map = map ;
        newDataSet.addAll(mySongs) ;

    }


    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listlayout, viewGroup, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currPos = getClickedPosition(v) ;
                //Toast.makeText(context, "This is " + currPos, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context , PlaySong.class) ;
                String currSong = localDataSet.get(currPos).getName() ;
                //Toast.makeText(context, localDataSet.size(), Toast.LENGTH_SHORT).show();
                intent.putExtra("songList", localDataSet);
                intent.putExtra("currentSong", currSong);
                intent.putExtra("position", currPos);
                intent.putExtra("map", map) ;

                context.startActivity(intent);
            }
        });


        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element

        viewHolder.getTextView().setText(localDataSet.get(position).getName().replace(".mp3",""));
        viewHolder.linearLayout.setTag(position);

    }

    private int getClickedPosition(View clickedView)
    {
        RecyclerView recyclerView = (RecyclerView) clickedView.getParent();
        ViewHolder currentViewHolder = (ViewHolder) recyclerView.getChildViewHolder(clickedView);
        return currentViewHolder.getAdapterPosition();
    }
    // Return the size of your dataset (invoked by the layout manager)

    public void update(ArrayList<Integer> indexArray) {
        localDataSet.clear();
        for (int i=0 ; i < indexArray.size() ; i++) {
            localDataSet.add(newDataSet.get(indexArray.get(i))) ;
        }
        notifyDataSetChanged();
        return ;
    }

    public void updatePlaylist(ArrayList<File> allSongs , ArrayList<Integer> indexArray) {
        localDataSet.clear();
        for (int i=0 ; i < indexArray.size() ; i++) {
            localDataSet.add(allSongs.get(indexArray.get(i))) ;
        }
        notifyDataSetChanged();
        return ;
    }

    public void resetDataSet() {
        localDataSet.clear();
        localDataSet.addAll(newDataSet) ;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @Override
    public Filter getFilter() {
        return songFilter;
    }

    private Filter songFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<File> filteredList = new ArrayList<>() ;

            if (constraint == null || constraint.length()==0) {
                filteredList.addAll(newDataSet) ;
            } else {
                String query = constraint.toString().toLowerCase().trim() ;


                for (File file : newDataSet) {
                    String s = file.getName().replace(".mp3" , "");
                    String original = s.toLowerCase().trim() ;


                    if (original.contains(query) || query.contains(original)) {
                        filteredList.add(file) ;
                    }
                }
            }

            FilterResults results = new FilterResults() ;
            results.values = filteredList ;

            return results ;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            localDataSet.clear();
            localDataSet.addAll((List) results.values) ;
            notifyDataSetChanged();
        }
    };
}
