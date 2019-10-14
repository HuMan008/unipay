package cn.gotoil.unipay.web.aspect;

import cn.gotoil.bill.web.helper.ServletRequestHelper;
import cn.gotoil.unipay.utils.UtilString;
import cn.gotoil.unipay.web.annotation.OpLog;
import cn.gotoil.unipay.web.services.OpLogService;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by Suyj <suyajiang@gotoil.cn> on 2017/3/17.10:57
 * 后台用户操作日志记录表
 */
@Aspect
@Component
public class OpLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(OpLogAspect.class);
    @Autowired
    private OpLogService opLogService;
    @Value("${isDebug}")
    private boolean isDebug;

    public static String getMthodDescp(JoinPoint joinPoint)
            throws Exception {
        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();

        Class targetClass = Class.forName(targetName);
        Method[] method = targetClass.getMethods();
        String methode = "";
        for (Method m : method) {
            if (m.getName().equals(methodName)) {
                Class[] tmpCs = m.getParameterTypes();
                if (tmpCs.length == arguments.length) {
                    OpLog methodCache = m.getAnnotation(OpLog.class);
                    if (methodCache != null) {
                        methode = methodCache.desc();
                    }
                    break;
                }
            }
        }
        return methode;
    }

/*    @AfterReturning("oplogAspect()")
    public void doAfterReturning(JoinPoint joinPoint) {
        System.out.println("doAfterReturning——-");
    }*/

    @Pointcut("@annotation(cn.gotoil.unipay.web.annotation.OpLog)")
    public void oplogAspect() {
    }

    @Before("oplogAspect()")
    public void beforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        if (isDebug) {
            logger.info("【BEFORE:】\n[Method]-->{}\n[args]-->{}", methodName, args);
        }
    }

    /*@AfterReturning(value="oplogAspect()",returning="result")
    public void afterReturning(JoinPoint joinPoint,Object result){
        String methodName =joinPoint.getSignature().getName();
        List<Object> args=Arrays.asList(joinPoint.getArgs());
        if(isDebug) System.out.println("Method - "+methodName+"  result with  :"+result);
    }*/

    /*
     * 在目标方法执行后（无论是否发生异常）  后置通知
     * 不能访问目标方法执行结果
     * */
    @After("oplogAspect()")

    public void afterMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        if (isDebug) {
            logger.info("【AFTER:】\n[Method]-->{}\n[args]-->{}", methodName, args);
        }
    }

    /*
     * 环绕通知需要携带 ProceedingJoinPoint 类型参数
     * 环绕通知类似于动态代理的全过程   ProceedingJoinPoint 类型的参数可以决定是否执行目标方法
     * 环绕通知 必须有返回值 且返回值就是方法的返回值
     * */

    @AfterThrowing(value = "oplogAspect()", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        List<Object> args = Arrays.asList(joinPoint.getArgs());
        if (isDebug) {
            logger.info("【AfterThrowing:】\n[Method]-->{}\n[args]-->{}\n[Exception]--->{}", methodName, args, ex);
        }
    }

    @AfterReturning(value = "oplogAspect()", returning = "result")
    public Object afterReturning(JoinPoint proceedingJoinPoint, Object result) {
        String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();

//        List<Object> args=Arrays.asList(proceedingJoinPoint.getArgs());
        Object[] argss = proceedingJoinPoint.getArgs();
        HttpServletRequest request = null;
        Object[] viewArgs = new Object[argss.length];
//        ActionMapping mapping = null;
        //通过分析aop监听参数分析出request等信息
        for (int i = 0; i < argss.length; i++) {
            if (argss[i] instanceof HttpServletRequest) {
                request = (HttpServletRequest) argss[i];
                viewArgs[i] = JSONObject.toJSONString(request.getParameterMap());
            } else if (argss[i] instanceof String) {
                viewArgs[i] = argss[i].toString();
            } else if (argss[i] instanceof Integer) {
                viewArgs[i] = ((Integer) argss[i]).intValue();
            } else if (argss[i] instanceof Long) {
                viewArgs[i] = ((Long) argss[i]).longValue();
            } else if (argss[i] instanceof Model) {
                viewArgs[i] = JSONObject.toJSONString((Model) argss[i]);
            } else {
                viewArgs[i] = argss[i].toString();
            }
        }
        List<Object> args = Arrays.asList(viewArgs);
        try {
            long begin = System.currentTimeMillis();
            String descp = getMthodDescp(proceedingJoinPoint);
            //前置通知
            if (isDebug) {
                logger.info("【AfterReturning:】\n[Method]-->{}\n[args]-->{}", methodName, args);
            }

            cn.gotoil.unipay.model.entity.OpLog opLog = new cn.gotoil.unipay.model.entity.OpLog();
            opLog.setCreatedAt(new Date());

            String token = ServletRequestHelper.httpServletRequest().getHeader("gtToken");
            String userId = JWT.decode(token).getClaim("id").asString();

            opLog.setOpUserName(userId);
            opLog.setCallMethod(className + "(" + methodName + ")");
            opLog.setMethodArgs(UtilString.getLongString(args.toString(), 4000));
            opLog.setMethodReturn(result == null ? "" : result.toString());
            opLog.setDescp(descp);
            opLogService.insert(opLog);


            //返回通知
            if (isDebug) {
                logger.info("【AfterReturning:CostTime->{}毫秒】\n[Method]-->{}\n[args]-->{}", begin - System.currentTimeMillis(), methodName, args);
            }

        } catch (Throwable e) {
            logger.error("className：{}；methodName：{};args:{},{}", className, methodName, args, e);
        }
        //后置通知
        return result;
    }

}
