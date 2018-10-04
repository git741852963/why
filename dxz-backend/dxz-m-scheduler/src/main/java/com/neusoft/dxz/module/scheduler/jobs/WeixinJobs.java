package com.neusoft.dxz.module.scheduler.jobs;

//import com.neusoft.pay.service.PayJobsService;
//import com.neusoft.shop.pay.dto.PaymentConfig;
//import com.neusoft.shop.pay.service.PayService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Created by Andy on 2015/3/25 0025.
 */
@Component
@Slf4j
public class WeixinJobs {

//    @Autowired
//    private PayService payService;
//
//    @Autowired
//    private PayJobsService weipayService;
//
//    @Scheduled(cron = "0 0/10 * * * *")
//    public void updateWeixinConfig() {
//
//        try {
//            log.info("[WEIXIN_JOBS] update access token and ticket start");
//            Response<PaymentConfig> response = payService.getPaymentConfig();
//            checkState(response.isSuccess(), response.getError());
//            checkArgument(notEmpty(response.getResult().getWeixinAppId()), "job.weixin.appid.empty");
//
//            weipayService.updateTokenAndTicket(response.getResult().getWeixinAppId(), response.getResult().getWeixinAppSecret());
//            log.info("[WEIXIN_JOBS] update access token and ticket finished");
//        } catch (Exception e) {
//            log.error("[WEIXIN_JOBS] update access token failed, error:{}", e);
//        }
//    }
}
