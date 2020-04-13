package cn.gotoil.unipay.utils;

import cn.gotoil.bill.exception.BillError;
import cn.gotoil.unipay.utils.UtilString;
import org.springframework.web.servlet.view.RedirectView;

/**
 * 页面跳转
 *
 * @author think <syj247@qq.com>、
 * @date 2020-2-26、15:25
 */
public class UtilPageRedirect {
    public static RedirectView makeErrorPage(BillError billError, String errorBackUrl) {
        String url =
                "/web/error?errorCode=" + billError.getCode() + "&errorMsg=" + UtilString.toUtf8(billError.getMessage())+"&backUrl="+UtilString.toUtf8(errorBackUrl);
        RedirectView redirectView = new RedirectView(url,true,false);
        return redirectView;
    }


    public static RedirectView makeErrorPage(int errorCode, String errorMsg,String errorBackUrl)  {
        String url  =
                "/web/error?errorCode=" + errorCode + "&errorMsg=" + UtilString.toUtf8(errorMsg)+"&backUrl="+UtilString.toUtf8(errorBackUrl) ;
        RedirectView redirectView = new RedirectView(url,true,false);
        return  redirectView;
    }

}


