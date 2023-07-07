package com.clement.service.impl;

import com.clement.dao.AccountMapper;
import com.clement.pojo.AccountDetail;
import com.clement.service.AccountDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author Qinghan Huang
 * @Date 2023/7/7 16:43
 * @Desc
 * @Version 1.0
 */
@Service
public class AccountDetailServiceImpl implements AccountDetailService {
    @Autowired
    AccountMapper accountMapper;
    @Override
    public String updateAccountDetail(AccountDetail accountDetail) {
        int i = accountMapper.UpdateAccountDetailByUsernameOrEmail(accountDetail);
        if(i>0){
            return null;
        }else{
            return "Failed to update account";
        }
    }
}
