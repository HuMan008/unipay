package cn.gotoil.unipay.web.controller.admin;

import cn.gotoil.unipay.web.services.AlipayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 测试
 *
 * @author think <syj247@qq.com>、
 * @date 2019-9-24、9:41
 */
@Controller
@RequestMapping("g2")
public class WebTestController {

    @Autowired
    AlipayService alipayService;

    @RequestMapping(value = "alipay", method = RequestMethod.GET)
    public ModelAndView alipayWebTest(Model model) {
        //        alipayService.pagePay()
        //       model.addAttribute("aa","as");
        model.addAttribute("error", "as");
        return new ModelAndView("alipay/submit");
    }
}
