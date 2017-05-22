package com.dda.a15.socmedoflife;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PostViewHolder>{

    List<Post> posts;

    RVAdapter(List<Post> posts){
        this.posts = posts;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view, parent, false);
        PostViewHolder pvh = new PostViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int i) {
        holder.name.setText(posts.get(i).name);
        holder.phrile.setText(posts.get(i).phrile);
        holder.text.setText(posts.get(i).text);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView phrile;
        TextView text;

        PostViewHolder(View itemView) {
            super(itemView);
            name= (TextView)itemView.findViewById(R.id.name);
            phrile = (TextView)itemView.findViewById(R.id.phrile);
            text = (TextView) itemView.findViewById(R.id.text);
        }
    }

}