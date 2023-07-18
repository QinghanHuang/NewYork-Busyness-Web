package com.clement.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PoiInfo {
    private int id;
    private String name;
    private String detailedLocation;
    private OpenTime openTime;
    private List<String> img = new ArrayList<>();
    private String introduction;
    private int busy; // current busy

    @Data
    public static class OpenTime{
        private String open;
        private String close;
    }

    public void setOpen(String open){
        if(openTime == null){
            openTime = new OpenTime();
        }
        openTime.setOpen(open);
    }

    public void setClose(String close){
        if(openTime == null){
            openTime = new OpenTime();
        }
        openTime.setClose(close);
    }

    public void setUrl1(String url1){
        img.add(url1);
    }

    public void setUrl2(String url2){
        img.add(url2);
    }

    public void setUrl3(String url3){
        img.add(url3);
    }
}
