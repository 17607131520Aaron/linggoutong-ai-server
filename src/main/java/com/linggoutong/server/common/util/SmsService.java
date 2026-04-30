package com.linggoutong.server.common.util;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.linggoutong.server.common.exception.BusinessException;
import com.linggoutong.server.common.result.ResultCode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class SmsService {

    @Value("${aliyun.sms.access-key-id:}")
    private String accessKeyId;

    @Value("${aliyun.sms.access-key-secret:}")
    private String accessKeySecret;

    @Value("${aliyun.sms.sign-name:}")
    private String signName;

    @Value("${aliyun.sms.template-code:}")
    private String templateCode;

    private Client client;
    private boolean enabled;

    @PostConstruct
    public void init() {
        this.enabled = StringUtils.hasText(accessKeyId) && StringUtils.hasText(accessKeySecret);
        
        if (!enabled) {
            log.warn("阿里云短信未配置，验证码将打印在控制台");
            return;
        }

        try {
            Config config = new Config()
                    .setAccessKeyId(accessKeyId)
                    .setAccessKeySecret(accessKeySecret)
                    .setEndpoint("dysmsapi.aliyuncs.com");
            this.client = new Client(config);
        } catch (Exception e) {
            log.warn("阿里云短信客户端初始化失败，验证码将打印在控制台: {}", e.getMessage());
            this.enabled = false;
        }
    }

    public void sendSmsCode(String phone, String code) {
        if (!enabled) {
            log.info("========================================");
            log.info("【短信验证码】手机号: {}", phone);
            log.info("【短信验证码】验证码: {}", code);
            log.info("========================================");
            return;
        }

        try {
            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(phone)
                    .setSignName(signName)
                    .setTemplateCode(templateCode)
                    .setTemplateParam("{\"code\":\"" + code + "\"}");

            SendSmsResponse response = client.sendSms(request);
            if (!"OK".equals(response.getBody().getCode())) {
                log.error("短信发送失败: {}", response.getBody().getMessage());
                throw new BusinessException(ResultCode.SYSTEM_ERROR, "短信发送失败");
            }

            log.info("短信发送成功: phone={}", phone);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("短信发送异常: {}", e.getMessage(), e);
            throw new BusinessException(ResultCode.SYSTEM_ERROR, "短信发送失败");
        }
    }
}
