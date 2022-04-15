package com.exam.workflowtest.config;

import com.exam.workflowtest.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * * 该接口的作用是，activiti在调用接口的时候，需要利用该接口与数据库打交道，
 * 如果没有实现该接口，在使用activiti提供的服务类进行查询的时候，会报错
 *
 * @author: RenLiLi
 * @date: 2022/4/13 18:07
 */
public class SelfUserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    public SelfUserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userService.findOneUserByName(username);
    }
}

