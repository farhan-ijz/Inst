package com.wurfel.yaqeen.inst.Interface;

/**
 * Created by aliapple on 9/13/17.
 */

public interface VolleyResponseListener {

    void onError(String message);

    void onResponse(Object response);
}