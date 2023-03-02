package com.xiaobin.yupaobackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 自定义Swagger文档的配置
 *
 * @Author: hongxiaobin
 * @Time: 2023/3/2 20:49
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 控制器配置
     *
     * @return Docket
     */
    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xiaobin.yupaobackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * API信息
     *
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("伙伴匹配系统")
                .description("伙伴匹配系统API接口文档")
                .termsOfServiceUrl("https://github.com/1binbin")
                .contact(new Contact("hongxiaobin", "https://github.com/1binbin", "2812181610@qq.com"))
                .version("1.0")
                .build();
    }
}