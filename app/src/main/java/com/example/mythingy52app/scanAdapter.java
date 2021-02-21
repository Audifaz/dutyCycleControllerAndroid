package com.example.mythingy52app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;

public class scanAdapter extends RecyclerView.Adapter<scanAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    public LinkedList<String> mList = new LinkedList<String>();
    final private ScanItemClickListener mOnClickListener;

    public scanAdapter(Context context, LinkedList<String> list, ScanItemClickListener onClickListener ){
        this.mList=list;
        this.mOnClickListener = onClickListener;
        mInflater= LayoutInflater.from(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView;
        public scanAdapter mAdapter;
        public ViewHolder(@NonNull View itemView, scanAdapter adapter) {
            super(itemView);
            this.mTextView=itemView.findViewById(R.id.scanText);
            itemView.setOnClickListener(this);
            this.mAdapter=adapter;
        }

        @Override
        public void onClick(View view) {
            int position = getLayoutPosition();
            mOnClickListener.onScanItemClick(position);
        }
    }

    @NonNull
    @Override
    public scanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mView = mInflater.inflate(R.layout.scanlist, parent, false);
        return new ViewHolder(mView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull scanAdapter.ViewHolder holder, int position) {
        String current = mList.get(position);
        holder.mTextView.setText(current);
    }

    @Override
    public int getItemCount() {
        if(mList==null){
        return 0;}
        else{
            return mList.size();
        }
    }

    //Interface for clicking individual objects
    interface ScanItemClickListener{
        void onScanItemClick(int position);
    }


}
