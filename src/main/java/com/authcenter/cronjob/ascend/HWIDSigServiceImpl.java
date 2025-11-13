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
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("ascend_hwid_sig")
public class HWIDSigServiceImpl implements CronjobServiceInter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HWIDSigServiceImpl.class);

    @Autowired
    private CasbinServiceContext casbinServiceContext;

    @Value("${enforcers.instances.ascend_gitcode_sig.service_name:}")
    private String casbinSigServiceName;

    @Value("${enforcers.instances.ascend_hwid_sig.service_name:}")
    private String casbinServiceName;

    /**
     * 工作台地址.
     */
    @Value("${ascend.workbench.url:}")
    private String workbenchUrl;

    /**
     * 工作台token.
     */
    @Value("${ascend.workbench.token:}")
    private String workbenchToken;

    @Override
    public void start() {
        syncHwidSig();
    }

    private void syncHwidSig() {
        LOGGER.info("sync ascend huaweiID sig start");
        try {
            Enforcer sigEnforcer = casbinServiceContext.getService(casbinSigServiceName);
            List<List<String>> policy = sigEnforcer.getPolicy();
            if (CollectionUtils.isEmpty(policy)) {
                return;
            }
            Map<String, String> hwidMap = new HashMap<>();
            Set<String> gitcodeIds = new HashSet<>();
            for (List<String> perf : policy) {
                gitcodeIds.add(perf.get(0));
            }
            for (String gitcodeId : gitcodeIds) {
                String hwId = getHwidByGitcodeId(gitcodeId);
                if (StringUtils.isBlank(hwId)) {
                    continue;
                }
                hwidMap.put(gitcodeId, hwId);
            }
            for (List<String> perf : policy) {
                addUserAuth(hwidMap.get(perf.get(0)), perf.get(1), perf.get(2));
            }
        } catch (Exception e) {
            LOGGER.error("parse ascend huaweiID sig failed: " + e.getMessage());
        }
        LOGGER.info("sync ascend huaweiID sig end");
    }

    private void addUserAuth(String userName, String sig, String role) {
        try {
            if (StringUtils.isAnyBlank(userName, sig, role)) {
                return;
            }
            Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
            if (!enforcerService.enforce(userName, sig, role)) {
                if (enforcerService.addPolicy(userName, sig, role)) {
                    LogUtil.createLogs("system", "add hwid sig", "auth-center",
                            "add " + userName,
                            "localhost", "success");
                } else {
                    LogUtil.createLogs("system", "add hwid sig", "auth-center",
                            "add " + userName,
                            "localhost", "false");
                }
            }
        } catch (Exception e) {
            LOGGER.error("add ascend hwid sig-{} failed {}", userName, e.getMessage());
        }
    }

    private String getHwidByGitcodeId(String gitcodeId) {
        try {
            HttpResponse<JsonNode> response = Unirest.get(workbenchUrl
                            + "/profile/getUserByThird?provider=gitcode&loginName=" + gitcodeId)
                    .header("token", workbenchToken)
                    .asJson();
            if (response.getStatus() == 400) {
                // 用户没绑定gitcode
                return null;
            }
            if (response.getStatus() != 200) {
                LOGGER.error("get user failed {}", response.getBody().toString());
                return null;
            }
            return response.getBody().getObject().getJSONObject("data").getString("username");
        } catch (Exception e) {
            LOGGER.error("get huweiId by gitcodeId-{} failed {}", gitcodeId, e.getMessage());
        }
        return null;
    }
}
