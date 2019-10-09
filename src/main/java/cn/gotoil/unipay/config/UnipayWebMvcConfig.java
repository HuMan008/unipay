package cn.gotoil.unipay.config;

import cn.gotoil.bill.config.BillWebMvcConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

//@Configuration
public class UnipayWebMvcConfig extends BillWebMvcConfig {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }
}
