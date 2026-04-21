package com.southwind.util;

public class CommonUtils {

    public static String getColor(String level){
        switch (level){
            case "一等奖":
                return "#ffd700";
            case "二等奖":
                return "#c0c0c0";
            case "三等奖":
                return "#cd7f32";
        }
        return null;
    }

    public static String getIcon(String type){
        switch (type){
            case "tech":
                return "el-icon-s-promotion";
            case "academic":
                return "el-icon-s-ticket";
            case "business":
                return "el-icon-s-platform";
        }
        return null;
    }

}
