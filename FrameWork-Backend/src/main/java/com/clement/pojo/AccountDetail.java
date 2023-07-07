package com.clement.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

/**
 * @Author Qinghan Huang
 * @Date 2023/6/27 22:57
 * @Desc
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountDetail {
    private int id;
    private String name;
    private Date birthday;
    private String introduction;
    private String gender;
    private String username;
    private String email;
    private String phoneNumber;
    private int avatar;



}
