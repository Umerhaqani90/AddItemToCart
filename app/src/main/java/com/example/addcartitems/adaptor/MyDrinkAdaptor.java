package com.example.addcartitems.adaptor;

import android.content.Context;
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
import com.example.addcartitems.listener.ICartLoadListener;
import com.example.addcartitems.listener.IRecyclerViewClickListener;
import com.example.addcartitems.model.CartModel;
import com.example.addcartitems.model.DrinkModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyDrinkAdaptor extends RecyclerView.Adapter<MyDrinkAdaptor.MyDrinkViewHolder> {
    private Context context;
    private List<DrinkModel> drinkModelList;
    private ICartLoadListener iCartLoadListener;

    public MyDrinkAdaptor(Context context, List<DrinkModel> drinkModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.drinkModelList = drinkModelList;
        this.iCartLoadListener = iCartLoadListener;

    }

//    public MyDrinkAdaptor (Context context,List<DrinkModel>drinkModelList)
//    {
//        this.context=context;
//        this.drinkModelList=drinkModelList;
//    }


    @NonNull
    @Override
    public MyDrinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyDrinkViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_drink_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyDrinkViewHolder holder, int position) {
        Glide.with(context)
                .load(drinkModelList.get(position).getImage())
                .into(holder.imageView);
        holder.txtPrice.setText(new StringBuilder("$").append(drinkModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(drinkModelList.get(position).getName()));

        holder.setClickListener((view, adapterPosition) -> {
            addToCart(drinkModelList.get(position));

        });
    }

    private void addToCart(DrinkModel drinkModel) {
        DatabaseReference userCart= FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child("UNIQUE_USER_ID");
        userCart.child(drinkModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) // if user already have item in Cart
                        {
                            //just update quantity and total price
                            CartModel cartModel=snapshot.getValue(CartModel.class);

                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            Map<String,Object> updateData=new HashMap<>();
                            updateData.put("Quantity",cartModel.getQuantity());
                            updateData.put("Total Price",cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));

                            userCart.child(drinkModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(unused -> iCartLoadListener.onCartLoadFailure("Add to Cart Success")).addOnFailureListener(e -> iCartLoadListener.onCartLoadFailure(e.getMessage()));

                        }
                        else // if item not have in cart, add new
                        {
                            CartModel cartModel=new CartModel();
                            cartModel.setName(drinkModel.getName());
                            cartModel.setImage(drinkModel.getImage());
                            cartModel.setKey(drinkModel.getKey());
                            cartModel.setPrice(drinkModel.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(drinkModel.getPrice()));

                            userCart.child(drinkModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(unused -> iCartLoadListener.onCartLoadFailure("Add to Cart Success")).addOnFailureListener(e -> iCartLoadListener.onCartLoadFailure(e.getMessage()));

                        }

                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailure(error.getMessage());

                    }
                });
    }

    @Override
    public int getItemCount() {
        return drinkModelList.size();
    }

    public class MyDrinkViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageView)
        ImageView imageView;
        @BindView(R.id.txtPrice)
        TextView txtPrice;
        @BindView(R.id.txtName)
        TextView txtName;

        IRecyclerViewClickListener clickListener;

        public void setClickListener(IRecyclerViewClickListener clickListener) {
            this.clickListener = clickListener;
        }

        private Unbinder unbinder;
        public MyDrinkViewHolder(@NonNull View itemView) {
            super(itemView);

            unbinder= ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            clickListener.OnRecyclerClick(view,getAdapterPosition());
        }
    }
}
