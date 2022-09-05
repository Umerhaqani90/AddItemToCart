package com.example.addcartitems.listener;

import com.example.addcartitems.model.DrinkModel;

import java.util.List;

public interface IDrinkLoadListener {
    void onDrinkLoadSuccess(List<DrinkModel>drinkModelList);
    void onDrinkLoadFailure(String message);
}
