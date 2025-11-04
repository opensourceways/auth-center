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

package com.authcenter.cronjob;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CronjobTaskInit {
    private static final Logger LOGGER = LoggerFactory.getLogger(CronjobTaskInit.class);

    @Value("${cronjob.services:}")
    private String cronjobServices;

    @Autowired
    private CronjobServiceContext cronjobServiceContext;

    public void execArgs() {
        List<String> services = getService();
        if (services == null || services.isEmpty()) {
            return;
        }

        for (String service : services) {
            execService(service);
        }
        System.exit(0);
    }

    /**
     * get service.
     *
     * @return list of services.
     */
    public List<String> getService() {
        if (StringUtils.isBlank(cronjobServices)) {
            return new ArrayList<>();
        }
        String[] splits = cronjobServices.split(",");
        return Arrays.stream(splits).filter(
                s -> !StringUtils.isBlank(StringUtils.trimToEmpty(s))
        ).collect(Collectors.toList());
    }

    /**
     * exec service.
     *
     * @param service service.
     */
    public void execService(String service) {
        CronjobServiceInter userCenterService = cronjobServiceContext.getUserCenterService(service);
        if (userCenterService == null) {
            LOGGER.warn("task-{} is not exist", service);
            return;
        }
        userCenterService.start();
    }
}
