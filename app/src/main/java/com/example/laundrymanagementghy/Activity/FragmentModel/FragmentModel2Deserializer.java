package com.example.laundrymanagementghy.Activity.FragmentModel;

import android.util.Log;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FragmentModel2Deserializer implements JsonDeserializer<FragmentModel2> {
    @Override
    public FragmentModel2 deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        FragmentModel2 fragmentModel = new FragmentModel2();
        JsonObject jsonObject = json.getAsJsonObject();

        JsonElement supplyDataElement = jsonObject.get("Getsupply_data");

        if (supplyDataElement != null) {
            if (supplyDataElement.isJsonArray()) {
                fragmentModel.mUserItems = new ArrayList<>();
                for (JsonElement element : supplyDataElement.getAsJsonArray()) {
                    FragmentModel2.mStoreItem item = context.deserialize(element, FragmentModel2.mStoreItem.class);
                    fragmentModel.mUserItems.add(item);
                }
            } else if (supplyDataElement.isJsonPrimitive() && supplyDataElement.getAsJsonPrimitive().isString()) {
                String errorMsg = supplyDataElement.getAsString();
                // Handle the error message as needed (e.g., log it, show a toast)
                Log.e("FragmentModel2Deserializer", "Error: " + errorMsg);
                fragmentModel.mUserItems = new ArrayList<>(); // Initialize as empty list
            } else {
                throw new JsonParseException("Unexpected type for Getsupply_data");
            }
        } else {
            // If Getsupply_data is missing, initialize as empty list
            fragmentModel.mUserItems = new ArrayList<>();
        }

        return fragmentModel;
    }
}
