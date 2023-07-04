package com.clement.pojo;

import lombok.Data;

@Data
public class PoiInfo {
    private String id;
    private String name;
    private String detailedLocation;
//    private String open;
//    private String close;
    private OpenTime openTime;
    private String busy;
//    private String url1;
//    private String url2;
//    private String url3;
    private Img img;
    private String introduction;

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

    @Data
    public static class Img{
        private String url1;
        private String url2;
        private String url3;
    }

    public void setUrl1(String url1){
        if(img == null){
            img = new Img();
        }
        img.setUrl1(url1);
    }

    public void setUrl2(String url2){
        if(img == null){
            img = new Img();
        }
        img.setUrl2(url2);
    }

    public void setUrl3(String url3){
        if(img == null){
            img = new Img();
        }
        img.setUrl3(url3);
    }
}
