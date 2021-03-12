package com.xch.demo.service;

import com.xch.demo.smscode.SmsCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Service
public class RedisCodeService {

    private final static String SMS_CODE_PREFIX = "SMS_CODE";
    private final static Integer TIME_OUT = 300;

    private static HashMap<String, String> redis;
    static{
        redis = new HashMap<>();
    }


//    @Autowired
//    private StringRedisTemplate redisTemplate;
    //模拟redis存储

    /**
     * 保存验证码到redis
     * @param smsCode 短信验证码
     * @param request
     * @param mobile
     * @throws Exception
     */
    public void save(SmsCode smsCode, ServletWebRequest request, String mobile) throws Exception {
//        redisTemplate.opsForValue().set(key(request, mobile), smsCode.getCode(), TIME_OUT, TimeUnit.SECONDS);
        redis.put(key(request, mobile), smsCode.getCode());
    }

    /**
     * 移除验证码
     *
     * @param request
     * @param mobile
     * @return
     * @throws Exception
     */
    public String get(ServletWebRequest request, String mobile) throws Exception {
        return redis.get(key(request, mobile));
//        return redisTemplate.opsForValue().get(key(request, mobile));
    }

    /**
     * 移除验证码
     * @param request
     * @param mobile
     * @throws Exception
     */
    public void remove(ServletWebRequest request, String mobile) throws Exception {
        redis.remove(key(request, mobile));
//        redisTemplate.delete(key(request, mobile));
    }



    private String key(ServletWebRequest request, String mobile) throws Exception {
        String deviceId = request.getHeader("deviceId");
        if (StringUtils.isBlank(deviceId)) {
            throw new Exception("请在请求头中设置deviceId");
        }
        return SMS_CODE_PREFIX + deviceId + ":" + mobile;
    }
}
