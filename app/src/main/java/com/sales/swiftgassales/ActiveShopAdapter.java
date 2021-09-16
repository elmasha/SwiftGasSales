package com.sales.swiftgassales;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;


import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveShopAdapter extends FirestoreRecyclerAdapter<ActiveShop, ActiveShopAdapter.ActiveViewHolder> {

    private OnItemCickListener listener;
    public Context context;

    public ActiveShopAdapter(@NonNull FirestoreRecyclerOptions<ActiveShop> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ActiveViewHolder holder, int position, @NonNull ActiveShop model) {
        holder.userName.setText(model.getVendorName());
        holder.shopName.setText(model.getShopName());
        holder.shopNo.setText(model.getShopNo());
        if(context != null | model.getImage() != null) {
            Picasso.with(context).load(model.getImage()).placeholder(R.drawable.load).error(R.drawable.errorimage).into(holder.userImage);
        }



    }

    @NonNull
    @Override
    public ActiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_row,parent,false);
        this.context = parent.getContext();
        return new ActiveViewHolder(v);
    }


    ///Delete item
    public void deleteItem (int position) {
     getSnapshots().getSnapshot(position).getReference().delete();
    }

    class ActiveViewHolder extends RecyclerView.ViewHolder{
       private TextView userName, shopNo, shopName;
       private View view;
       private CircleImageView userImage;

        public ActiveViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.ActiveVendorNameR);

            shopNo = itemView.findViewById(R.id.ActiveShopNoR);
            shopName = itemView.findViewById(R.id.ActiveShopNameR);
            userImage = itemView.findViewById(R.id.ShopImageR);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int position = getAdapterPosition();

                    if (position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position),position);


                    }
                }
            });



        }
    }


    public interface  OnItemCickListener{
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemCickListener listener){

        this.listener = listener;

    }




}
