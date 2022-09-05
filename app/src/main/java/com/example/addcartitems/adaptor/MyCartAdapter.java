package com.example.addcartitems.adaptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.addcartitems.R;
import com.example.addcartitems.adaptor.eventbus.MyUpdateCartEvent;
import com.example.addcartitems.model.CartModel;
import com.google.firebase.database.FirebaseDatabase;


import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder> {

    private Context context;
    private List<CartModel>cartModelList;

    public MyCartAdapter(Context context, List<CartModel> cartModelList) {
        this.context = context;
        this.cartModelList = cartModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_cart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(cartModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("$").append(cartModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(cartModelList.get(position).getName()));
        holder.txtQuantity.setText(new StringBuilder().append(cartModelList.get(position).getQuantity()));

        holder.btnMinus.setOnClickListener(view -> {
            minusCartItem(holder,cartModelList.get(position));
        });

        holder.btnPlus.setOnClickListener(view -> {
            plusCartItem(holder,cartModelList.get(position));
        });

        holder.btnDelete.setOnClickListener(view -> {
            AlertDialog alertDialog=new AlertDialog.Builder(context)
                    .setTitle("Delete Item")
                    .setMessage("Do You realy want to Delete this item")
                    .setNegativeButton("CANCEL", (alertDialog12, i) -> alertDialog12.dismiss())
                    .setPositiveButton("OK", (alertDialog1, i) -> {

                        //Temp remove
                        notifyItemRemoved(position);

                        deleteFromFirebase(cartModelList.get(position));
                        alertDialog1.dismiss();

                    }).create();
            alertDialog.show();
        });

    }

    private void deleteFromFirebase(CartModel cartModel) {
        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .removeValue()
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));


    }

    private void plusCartItem(MyViewHolder holder, CartModel cartModel) {

        cartModel.setQuantity(cartModel.getQuantity()+1);
        cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

        //updateQuantity
        holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
        updateFirebase(cartModel);
    }

    private void minusCartItem(MyViewHolder holder, CartModel cartModel) {
        if(cartModel.getQuantity()>1)
        {
            cartModel.setQuantity(cartModel.getQuantity()-1);
            cartModel.setTotalPrice(cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

            //updateQuantity
            holder.txtQuantity.setText(new StringBuilder().append(cartModel.getQuantity()));
            updateFirebase(cartModel);
        }
    }

    private void updateFirebase(CartModel cartModel) {

        FirebaseDatabase.getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID")
                .child(cartModel.getKey())
                .setValue(cartModel)
                .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));

    }


    @Override
    public int getItemCount() {
        return cartModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnMinus)
        ImageView btnMinus;

        @BindView(R.id.btnPlus)
        ImageView btnPlus;

        @BindView(R.id.btnDelete)
        ImageView btnDelete;

        @BindView(R.id.imageView)
        ImageView imageView;

        @BindView(R.id.txtName)
        TextView txtName;

        @BindView(R.id.txtPrice)
        TextView txtPrice;

        @BindView(R.id.txtQuantity)
        TextView txtQuantity;

        Unbinder unbinder;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder= ButterKnife.bind(this,itemView);
        }
    }
}
