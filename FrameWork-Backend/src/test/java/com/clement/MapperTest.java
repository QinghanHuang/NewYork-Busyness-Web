package com.clement;

import com.clement.dao.AccountMapper;
import com.clement.dao.PoiMapper;
import com.clement.pojo.AccountDetail;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;

/**
 * @Author Qinghan Huang
 * @Date 2023/6/28 13:14
 * @Desc
 * @Version 1.0
 */
@SpringBootTest
public class MapperTest {
    @Resource
    AccountMapper accountMapper;

    @Resource
    PoiMapper poiMapper;
    @Test
    public void test(){
        AccountDetail clement = accountMapper.findAccountDetailByUsernameOrEmail("clement");
        clement.setAvatar(2);
        clement.setBirthday(Date.valueOf("2001-08-18"));
        accountMapper.UpdateAccountDetailByUsernameOrEmail(clement);
    }
    @Test
    public void test1(){
        System.out.println(poiMapper.getAreaByPid(12));

    }
}

