/* This project is licensed under the Mulan PSL v2.
 You can use this software according to the terms and conditions of the Mulan PSL v2.
 You may obtain a copy of Mulan PSL v2 at:
     http://license.coscl.org.cn/MulanPSL2
 THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 PURPOSE.
 See the Mulan PSL v2 for more details.
 Create: 2025
*/

package com.authcenter.tasks;

import com.authcenter.application.casbin.CasbinServiceContext;
import com.authcenter.common.config.CasbinConfig;
import jakarta.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

@Component
public class CasbinLoadPolicy {
    /**
     * 定时任务线程池.
     */
    @Autowired
    @Qualifier("SR-Task-SchedulePool")
    private ThreadPoolTaskScheduler taskPool;

    @Value("${cronjob.services:}")
    private String cronjobServices;

    /**
     * casbin配置.
     */
    @Autowired
    private CasbinConfig casbinConfig;

    /**
     * casbin上下文.
     */
    @Autowired
    private CasbinServiceContext casbinServiceContext;

    @PostConstruct
    private void init() {
        if (StringUtils.isBlank(cronjobServices)) {
            taskPool.schedule(this::loadPolicy, new CronTrigger("0 0/5 * * * ?"));
        }
    }

    private synchronized void loadPolicy() {
        for (String service : casbinConfig.getServiceInfo().keySet()) {
            Enforcer enforcerService = casbinServiceContext.getService(service);
            enforcerService.loadPolicy();
        }
    }
}
