package com.huaneng.data.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;

/**
 * @Description: mybatis plus config
 * @Author: xumingrui
 * @CreateDate: 2019/12/5 下午6:40
 */
@Configuration
@MapperScan("com.huaneng.data.mapper")
public class MybatisPlusConfig {
    /**
     * 分页插件,自动识别数据库类型
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        // tenantInterceptor.setTenantLineHandler(damengTenantLineHandler);
        // interceptor.addInnerInterceptor(tenantInterceptor);

        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public ConfigurationCustomizer configurationCustomizer() {
        return configuration -> configuration.setUseGeneratedShortKey(false);
    }
}
