package com.exam.workflowtest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * * 用户
 *
 * @author: RenLiLi
 * @date: 2022/4/14 10:01
 */
@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User findOneUserByName(String username) {
        logger.info("username:");
        logger.info(username);
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        // 密码置空
        return new User(username, "", authorities);
    }
}
