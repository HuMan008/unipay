package cn.gotoil.unipay.config;

import cn.gotoil.bill.config.BillWebMvcConfig;
import cn.gotoil.bill.config.property.BillProperties;
import cn.gotoil.bill.web.filter.HttpBodyStreamWrapperFilter;
import org.springframework.beans.factory.annotation.Autowired;

//@Configuration
public class UnipayWebMvcConfig extends BillWebMvcConfig {
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
//        super.addResourceHandlers(registry);
//    }

    @Autowired
    @SuppressWarnings("all")
    private HttpBodyStreamWrapperFilter wrapperFilter;
    @SuppressWarnings("all")
    @Autowired
    private BillProperties billProperties;


}
