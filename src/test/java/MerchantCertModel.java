import lombok.Getter;
import lombok.Setter;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @author think <syj247@qq.com>、
 * @date 2021/1/7、11:13
 */
@Getter
@Setter
public class MerchantCertModel {

    private String serialNumber;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public MerchantCertModel(String serialNumber, PublicKey publicKey, PrivateKey privateKey) {
        this.serialNumber = serialNumber;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


}
