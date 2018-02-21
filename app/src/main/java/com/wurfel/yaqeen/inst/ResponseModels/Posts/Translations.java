
package com.wurfel.yaqeen.inst.ResponseModels.Posts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Translations implements Serializable {

    @SerializedName("ar")
    @Expose
    private Integer ar;
    @SerializedName("en")
    @Expose
    private Integer en;

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Integer getEn() {
        return en;
    }

    public void setEn(Integer en) {
        this.en = en;
    }

}
