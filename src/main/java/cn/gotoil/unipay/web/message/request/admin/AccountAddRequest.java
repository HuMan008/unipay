package cn.gotoil.unipay.web.message.request.admin;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class AccountAddRequest {



    @NotNull
    @Size(max = 20)
    @ApiModelProperty(required = true, example = "", value = "名称", position = 1)
    String name;

    @Size(max = 11)
    @ApiModelProperty(required = true, example = "", value = "id", position = 2)
    Integer id;

    @NotNull
    @Size(max = 20)
    @ApiModelProperty(required = true, example = "", value = "支付类型", position = 3)
    String payType;

    @NotNull
    @ApiModelProperty(required = true, example = "", value = "配置", position = 5)
    String configJson;

    @NotNull
    @Size(max = 1)
    @ApiModelProperty(required = true, example = "", value = "状态", position = 7,
            dataType="cn.gotoil.unipay.model.enums.EnumStatus")
    String status="{\"appID\":\"2018062860442358\",\"pubKey\":\"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh6ax9uMINGAWZWEl3Y3dbnD7LCREoQIcATbfLrIZooBtpfB0Yd/rDrg3eBZW+9cacFDiHkbOFpWNFIs6NLTAH1qm0EnaTKDsGxQQv6Fn+6QHvCH2H1s/wR7BIlQAQv8SEIt3MOuTb0PzUMHzs9zDdnARhA1vupnUaBsRU5lkE/0heCMgxTjkWzJtoLIaaFpH5Zf8fczoU1EaS3pI2KYh5E7oWtcppl+7ZkrocncQwkh+LfVQBTF+/89hRpLbf/j0iCes/M3lesaw2O0YQV213mXEIFp/onD/3LdJu8BHAifgTxexgrqWGYiQHFxQzQpRMNN51M8Sy2pF7GOG9YAI9QIDAQAB\",\"priKey\":\"MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQD+QG9HEpm5gpvu9619OM+e8ihigkU+JHDVGQScy0++ZMV0obPU8rUHdEl5YUhi+WPV0N9m7QdyUzvh0yjwnEUZ/gM6xy2SRmwwatDyK9+/N1kDr431ZAjlJ/HxqpFuSZr1Zh3gJVNgGechdhUnm9mwq8ErElKve5ZVKTHacd8M82UJw4o77cVfMdTo57bc5Xg3mTi1yw8PEdt5WHyhxWaw2kD9L+NdPPwSDWJXpqTTlu1dNywfY/tyKKrGuKdUEOD2IUpPmxG+IK7rNoD/IurwIGEx0CcjZZn31tsBraJxpC+UbB5glVgagGOmrjmUf4is+NXy+ZsKtN2q0V+QiMenAgMBAAECggEBALGhnNz/3syftofXHKte9JcswhBIyU2oMfh/uRw+YyE20Eky7Cv7wYRKt0aJNASKDjkE6//B3DEAe9bDK1gnJZQKRoFbRLhsYQvWpNJ82p6CcbtEfJ6T3C8VoFBiBacS36NRS9Ac48pBbpyPHiqFFSr8ZEHOVFxrsNUgqcbfRaGKSnEoQOI9PRdEq3MK77C30v7S1IhwCKloxwZ9gap0qHLmaTdVXvUURzgJNYaS6nqDQarYegfkeIvfsidKvW45Vs1P2oK5vzVrz3AFZFzd4zlvUyk9WaX66KWzDApX8aninRL4OrHy3dfLxZDdr3Cd17kBWoTddyNzrmJaa3avpQECgYEA/zVQzMrEZp2H374fVV9e1lr49Fzi7+KMja/DfRxQJexEWnu6f6ky676tf9tLOm/Rqum4iqV+Q/jFZkVzOfOKr+1TNIxxDulZJua6C/1e7toIzw4pYD4bHbtbITBX/L0yeEKjm+KnxYXQl0GgF+k0PH99nGzpBx93pd08csnGMoECgYEA/wpb/sKbSBCg4RoszHBfbGbRGnIvwSVSj+gHOTuhmLrL2pv34l90qvC9TDtJCvx8zay64ZfI2lrro99ewDzJgLLMiBvCZDXllwOJYD7kSLXql7i8R+zluAedkf11rFCDRyoIzFO6klkN2wdYyx9onY0tpSmWg0NyCFFNwEsAFicCgYAgm/llEYui6QIRjsOGLxd5IEqrPqsop7P57rl15QZAsj3RqfmckkwVX1nf+m9UWjFaO17/KEJIASOH+9vLTdksvZBiKIfRtfNUSyr+mTNmJan2ewLCb20bkXMFfJ3hh3NJdopdQsrZLE4I5bPFGmE4UboFIM1twZGSOgCdUjbnAQKBgA5yB8PmQeVTC8m0MdViiAUbzbwxu4K1GngMLFyRhBWifuli7CdmQLVt7UrkEL8OjgMYRX2Bm17Cx5YAkN1SlBc2CV+sMtZ6NwbWrHFHHqb/psnxLLjmMhy6UbJzKLcZ2FWvi9AG7gpcAOzm/aCDpulFldBFCsFkkJDoCf9aQCcBAoGBAMhdmsa6K2VXZV4TM/23Fv7H7QOhhAJ6qB8nzE7Gr9DsYl+RpzpPXmAzfMoZUlVgFBR3eVfJ/a3WMzqpNwcZU9aWnV+nlpGhuTu+IAOJ/d2auYcUmT+H/EjtqqjjCl9X9xiSdJLc4B/w094O4v6fArB8+TQ6fq7tt4scLo6z+BIG\"}";
}
