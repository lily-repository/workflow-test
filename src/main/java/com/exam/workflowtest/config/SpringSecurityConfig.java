package com.exam.workflowtest.config;

import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Component;

/**
 * * 编写 WebSecurityConfigurerAdapter抽象类的实现子类并配置注解@Component
 * 在SpringBoot容器中，如果不存在该WebSecurityConfigurerAdapter的实现类，启动的时候，会报错
 *
 * @author: RenLiLi
 * @date: 2022/4/13 18:05
 */
@Component
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
}
