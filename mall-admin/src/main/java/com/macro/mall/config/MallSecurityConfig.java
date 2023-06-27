package com.macro.mall.config;

import com.macro.mall.model.UmsResource;
import com.macro.mall.security.component.DynamicSecurityService;
import com.macro.mall.service.UmsAdminService;
import com.macro.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * mall-security模块相关配置
 * Created by macro on 2019/11/9.
 */
@Configuration
public class MallSecurityConfig {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsResourceService resourceService;

    /**
     * @author juzi
     * @date 2023/6/26 下午 6:48
     * @description 重写的方法
     */
    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> adminService.loadUserByUsername(username);
    }

    // 和上面那个方法相同的，相当于重写了UserDetailsService 里的方法
    @Bean
    public UserDetailsService userDetailsService2() {
        //定义UserDetailsService实现类
        UserDetailsService detailsService = new UserDetailsService() {
            //实现loadUserByUsername方法
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                //调用adminService.loadUserByUsername方法获取用户信息
                UserDetails userDetails = adminService.loadUserByUsername(username);
                if (userDetails == null) {
                    throw new UsernameNotFoundException("用户不存在！");
                }
                return userDetails;
            }
        };
        return detailsService;
    }

    @Bean
    public DynamicSecurityService dynamicSecurityService() {
        return () -> {
            Map<String, ConfigAttribute> map = new ConcurrentHashMap<>();
            List<UmsResource> resourceList = resourceService.listAll();
            for (UmsResource resource : resourceList) {
                map.put(resource.getUrl(), new org.springframework.security.access.SecurityConfig(resource.getId() + ":" + resource.getName()));
            }
            return map;
        };
    }
}
