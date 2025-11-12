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

package com.authcenter.cronjob.ascend;

import com.authcenter.application.casbin.CasbinServiceContext;
import com.authcenter.common.utils.LogUtil;
import com.authcenter.cronjob.CronjobServiceInter;
import org.apache.commons.lang3.StringUtils;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service("ascend_hwid_meeting")
public class HWIDMeetingServiceImpl implements CronjobServiceInter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HWIDMeetingServiceImpl.class);

    private static final String MEETING_CREATOR = "creator";

    @Autowired
    private CasbinServiceContext casbinServiceContext;

    @Value("${enforcers.instances.ascend_hwid_sig.service_name:}")
    private String casbinSigServiceName;

    @Value("${enforcers.instances.ascend_hwid_meeting.service_name:}")
    private String casbinServiceName;

    @Override
    public void start() {
        syncMeetingAuth();
    }

    private void syncMeetingAuth() {
        LOGGER.info("sync ascend huaweiID meeting start");
        try {
            Enforcer sigEnforcer = casbinServiceContext.getService(casbinSigServiceName);
            List<List<String>> policy = sigEnforcer.getPolicy();
            if (CollectionUtils.isEmpty(policy)) {
                return;
            }
            for (List<String> perf : policy) {
                addUserAuth(perf.get(1), perf.get(0));
            }
        } catch (Exception e) {
            LOGGER.error("parse ascend huaweiID meeting failed: " + e.getMessage());
        }
        LOGGER.info("sync ascend huaweiID meeting end");
    }

    private void addUserAuth(String sig, String userName) {
        try {
            if (StringUtils.isAnyBlank(sig, userName)) {
                return;
            }
            Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
            if (!enforcerService.enforce(userName, sig, MEETING_CREATOR)) {
                if (enforcerService.addPolicy(userName, sig, MEETING_CREATOR)) {
                    LogUtil.createLogs("system", "add auth", "auth-center",
                            "add " + userName,
                            "localhost", "success");
                } else {
                    LogUtil.createLogs("system", "add auth", "auth-center",
                            "add " + userName,
                            "localhost", "false");
                }
            }
        } catch (Exception e) {
            LOGGER.error("add ascend meeting auth failed {}", e.getMessage());
        }
    }
}
