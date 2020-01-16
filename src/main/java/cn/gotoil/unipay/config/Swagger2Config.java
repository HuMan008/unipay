package cn.gotoil.unipay.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * api文档配置
 *
 * @author think <syj247@qq.com>、
 * @date 2019-4-10、8:58
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    private boolean isDebug =true;

    @Bean
    public Docket createWebApi() {
        ParameterBuilder tokenPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        tokenPar.name("gtToken").description("令牌").modelRef(new ModelRef("string")).parameterType("header").required(false).build();
        pars.add(tokenPar.build());
        if(isDebug) {

            return new Docket(DocumentationType.SWAGGER_2)
                    //                .globalOperationParameters(pars)
                    .apiInfo(webApiInfo()).groupName("WebApi接口文档")
                    //                .pathMapping("/web")
                    .select()
                    //为当前包路径
                    .apis(RequestHandlerSelectors.basePackage("cn.gotoil.unipay.web.controller.admin")).paths(PathSelectors.any()).build().securitySchemes(securitySchemes()).securityContexts(securityContexts());
        }else{
            return new Docket(DocumentationType.SWAGGER_2);
        }
    }

    @Bean
    public Docket createRestApi() {
        if(isDebug) {
            return new Docket(DocumentationType.SWAGGER_2).apiInfo(restApiInfo()).groupName("RestApi接口文档")
                    //                .pathMapping("/api/v1")
                    .select()
                    //为当前包路径
                    .apis(RequestHandlerSelectors.basePackage("cn.gotoil.unipay.web.controller.api.v1")).paths(PathSelectors.any()).build();
        }else{
            return null;
        }
    }


    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList();
        apiKeyList.add(new ApiKey("gtToken", "gtToken", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build());
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("gtToken", authorizationScopes));
        return securityReferences;
    }


    //构建 api文档的详细信息函数,注意这里的注解引用的是哪个
    private ApiInfo webApiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("webApi接口文档及测试")
                //创建人
//                .contact(new Contact("cookie", "http://www.baidu.com", "2118119173@qq.com"))
                //版本号
                .version("1.0")
                //描述
                .description("webApi接口")
                .build();
    }

    private ApiInfo restApiInfo() {
        return new ApiInfoBuilder()
                //页面标题
                .title("RestApi接口文档及测试")
                //创建人
//                .contact(new Contact("苏亚江", "http://www.baidu.com", "2118119173@qq.com"))
                //版本号
                .version("1.0")
                //描述
                .description("RestApi接口")
                .build();
    }
}
