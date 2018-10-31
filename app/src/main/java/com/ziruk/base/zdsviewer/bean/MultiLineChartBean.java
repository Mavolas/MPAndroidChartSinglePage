package com.ziruk.base.zdsviewer.bean;

import java.util.ArrayList;
import java.util.List;

import zds.io.client.TagValue;

/**
 * Created by 宋棋安
 * on 2018/10/30.
 */
public class MultiLineChartBean {

    private String[] names;

    private int[] colors;

    private  List<List<TagValue>> multitagvalue;


    public MultiLineChartBean() {

    }

    public String[] getNames() {
        return names;
    }

    public int[] getColors() {
        return colors;
    }

    public List <List <TagValue>> getMultitagvalue() {
        return multitagvalue;
    }


    public void setNames(String[] names) {
        this.names = names;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setMultitagvalue(List <List <TagValue>> multitagvalue) {
        this.multitagvalue = multitagvalue;
    }


}
