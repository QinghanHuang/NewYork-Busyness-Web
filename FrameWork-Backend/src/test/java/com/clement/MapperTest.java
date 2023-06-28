package com.clement;

import com.clement.dao.AccountMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
    @Test
    public void test(){
        accountMapper.findAccountDetailByUsernameOrEmail("TEST123");

    }
}

