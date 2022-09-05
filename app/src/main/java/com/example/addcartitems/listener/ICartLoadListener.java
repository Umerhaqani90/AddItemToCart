package com.example.addcartitems.listener;

import com.example.addcartitems.model.CartModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailure(String message);
}
