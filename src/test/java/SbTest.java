import cn.gotoil.unipay.UnipayApplication;
import cn.gotoil.unipay.web.helper.RedissonLockHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/3/3、17:00
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UnipayApplication.class)
public class SbTest {

    @Test
    public void t1() {
        String lock = "RedssionLockHelperABC";
        boolean x = RedissonLockHelper.tryLock(lock);
        System.out.println(x);
        System.out.println(RedissonLockHelper.isLocked(lock));
        for (int i = 0; i < 10; i++) {
            System.out.println(RedissonLockHelper.tryLock(lock));

        }
        RedissonLockHelper.unlock(lock);
        System.out.println(RedissonLockHelper.isLocked(lock));
    }
}
