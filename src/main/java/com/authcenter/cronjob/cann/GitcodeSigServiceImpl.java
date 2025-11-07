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

package com.authcenter.cronjob.cann;

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

@Service("cann_gitcode_sig")
public class GitcodeSigServiceImpl implements CronjobServiceInter {
    private static final Logger LOGGER = LoggerFactory.getLogger(GitcodeSigServiceImpl.class);

    private static final String SIG_MAINTAINER = "maintainer";

    private static final String SIG_COMMITTER = "committer";

    @Value("${datastat.sig.url:}")
    private String sigUrl;

    @Autowired
    private CasbinServiceContext casbinServiceContext;

    @Value("${enforcers.instances.cann_gitcode_sig.service_name:}")
    private String casbinServiceName;

    @Override
    public void start() {
        syncSig();
    }

    private void syncSig() {
        LOGGER.info("sync cann gitcode sig start");
        try {
            HttpResponse<JsonNode> responseUser = Unirest.get(sigUrl + "cann")
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
            LOGGER.error("parse cann gitcode sig failed: " + e.getMessage());
        }
        LOGGER.info("sync cann gitcode sig end");
    }

    private void parseSigList(JSONArray sigList) {
        if (sigList == null) {
            return;
        }
        for (int i =0; i < sigList.length(); i++) {
            String sigName = sigList.getJSONObject(i).getString("name");
            sigName = "sig-" + sigName;
            if (sigList.getJSONObject(i).has("committers")) {
                JSONArray committers = sigList.getJSONObject(i).getJSONArray("committers");
                parseCommitters(sigName, committers);
            }
            if (sigList.getJSONObject(i).has("maintainers")) {
                JSONArray maintainers = sigList.getJSONObject(i).getJSONArray("maintainers");
                parseMaintainers(sigName, maintainers);
            }
        }
    }

    private void parseCommitters(String sigName, JSONArray committers) {
        if (committers == null || committers.length() == 0) {
            return;
        }
        for (int j = 0; j < committers.length(); j++) {
            String gitcodeId = committers.getString(j);
            if (StringUtils.isBlank(gitcodeId)) {
                continue;
            }
            Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
            if (!enforcerService.enforce(gitcodeId, sigName, SIG_COMMITTER)) {
                if (enforcerService.addPolicy(gitcodeId, sigName, SIG_COMMITTER)) {
                    LogUtil.createLogs("system", "add committer", "auth-center",
                            "add " + gitcodeId,
                            "localhost", "success");
                } else {
                    LogUtil.createLogs("system", "add committer", "auth-center",
                            "add " + gitcodeId,
                            "localhost", "false");
                }
            }
        }
    }

    private void parseMaintainers(String sigName, JSONArray maintainers) {
        if (maintainers == null || maintainers.length() == 0) {
            return;
        }
        for (int j = 0; j < maintainers.length(); j++) {
            String gitcodeId = maintainers.getString(j);
            if (StringUtils.isBlank(gitcodeId)) {
                continue;
            }
            Enforcer enforcerService = casbinServiceContext.getService(casbinServiceName);
            if (!enforcerService.enforce(gitcodeId, sigName, SIG_MAINTAINER)) {
                if (enforcerService.addPolicy(gitcodeId, sigName, SIG_MAINTAINER)) {
                    LogUtil.createLogs("system", "add maintainer", "auth-center",
                            "add " + gitcodeId,
                            "localhost", "success");
                } else {
                    LogUtil.createLogs("system", "add maintainer", "auth-center",
                            "add " + gitcodeId,
                            "localhost", "false");
                }
            }
        }
    }
}
