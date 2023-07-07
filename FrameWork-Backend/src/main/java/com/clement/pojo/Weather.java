package com.clement.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.Serializable;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/7 13:51
 * @Desc
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Weather  implements Serializable {
    private Long timeStamp;
    private String timeString;
    private double temp;
    private int humidity;
    private double windSpeed;
    private int weatherId;
    private String weatherMain;
    private String weatherDes;

}
