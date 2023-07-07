package com.clement.controller;

import com.clement.dao.AccountMapper;
import com.clement.pojo.AccountDetail;
import com.clement.pojo.RestBean;
import com.clement.service.AccountDetailService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author Qinghan Huang
 * @Date 2023/6/27 22:56
 * @Desc
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    AccountDetailService accountDetailService;

    @GetMapping("/me")
    public RestBean<AccountDetail> me(@SessionAttribute("account") AccountDetail accountDetail){
        return RestBean.succcess(accountDetail);
    }
    @PostMapping("/me")
    public RestBean<String> updateAccount(HttpSession session,@RequestBody AccountDetail accountDetail){
        String s = accountDetailService.updateAccountDetail(accountDetail);
        if(s==null){
            session.setAttribute("account",accountDetail);
            return RestBean.succcess("Update User Success");
        }else{
            return RestBean.failure(500,s);
        }


    }

}
