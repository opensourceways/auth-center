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
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.casbin.jcasbin.main.Enforcer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ascend_sig_resource")
public class GitcodeSigResourceServiceImpl implements CronjobServiceInter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitcodeSigResourceServiceImpl.class);

    private static final String SIG_MAILING_LIST = "mailing_list";

    private static final String SIG_MEETING_URL = "meeting_url";

    @Value("${datastat.sig.url:}")
    private String sigUrl;

    @Value("${ascend.meeting.etherpad.url:}")
    private String etherpadUrl;

    @Autowired
    private CasbinServiceContext casbinServiceContext;

    @Value("${enforcers.instances.ascend_sig_resource.service_name:}")
    private String casbinServiceName;

    @Override
    public void start() {
        syncSig();
    }

    private void syncSig() {
        LOGGER.info("sync ascend gitcode sig resource start");
        try {
            HttpResponse<JsonNode> responseUser = Unirest.get(sigUrl + "ascend")
                    .header("Accept", "application/json")
                    .asJson();

            JSONObject sigDatas = null;
            if (responseUser.getStatus() == 200) {
                sigDatas = responseUser.getBody().getObject();
            } else {
                LOGGER.error("get sig info from datastat failed: " + responseUser.getBody().toString());
                return;
            }
            if (!sigDatas.has("data")) {
                LOGGER.error("sig info is null: " + responseUser.getBody().toString());
                return;
            }
            JSONArray sigList = sigDatas.getJSONArray("data");
            parseSigList(sigList);
        } catch (Exception e) {
            LOGGER.error("parse ascend gitcode sig resource failed: " + e.getMessage());
        }
        LOGGER.info("sync ascend gitcode sig resource end");
    }

    private void parseSigList(JSONArray sigList) {
        if (sigList == null) {
            return;
        }
        for (int i =0; i < sigList.length(); i++) {
            String sigName = sigList.getJSONObject(i).getString("name");
            sigName = "sig-" + sigName;
            if (sigList.getJSONObject(i).has(SIG_MAILING_LIST)) {
                String mailList = sigList.getJSONObject(i).getString(SIG_MAILING_LIST);
                if (isDeleteOthers(sigName, mailList, SIG_MAILING_LIST)) {
                    Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
                    enforcerService.removeFilteredPolicy(0, sigName, "", SIG_MAILING_LIST);
                }
                saveResource(sigName, mailList, SIG_MAILING_LIST);
            }
            if (sigList.getJSONObject(i).has(SIG_MEETING_URL)) {
                String meetingUrl = sigList.getJSONObject(i).getString(SIG_MEETING_URL);
                if (StringUtils.isBlank(meetingUrl) || "NA".equals(meetingUrl)) {
                    meetingUrl = etherpadUrl + sigName;
                }
                if (isDeleteOthers(sigName, meetingUrl, SIG_MEETING_URL)) {
                    Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
                    enforcerService.removeFilteredPolicy(0, sigName, "", SIG_MEETING_URL);
                }
                saveResource(sigName, meetingUrl, SIG_MEETING_URL);
            }
        }
    }

    private boolean isDeleteOthers(String sigName, String obj, String type) {
        if (StringUtils.isAnyBlank(sigName, obj, type) || "NA".equals(obj)) {
            return false;
        }
        Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
        List<List<String>> filteredPolicy = enforcerService.getFilteredPolicy(0, sigName, "", type);
        if (filteredPolicy.size() > 0 && !enforcerService.enforce(sigName, obj, type)) {
            return true;
        }
        return false;
    }

    private void saveResource(String sigName, String obj, String type) {
        if (StringUtils.isAnyBlank(sigName, obj, type) || "NA".equals(obj)) {
            return;
        }

        Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
        if (!enforcerService.enforce(sigName, obj, type)) {
            if (enforcerService.addPolicy(sigName, obj, type)) {
                LogUtil.createLogs("system", "add sig resource", "auth-center",
                        "add " + sigName,
                        "localhost", "success");
            } else {
                LogUtil.createLogs("system", "add sig resource", "auth-center",
                        "add " + sigName,
                        "localhost", "false");
            }
        }
    }
}
