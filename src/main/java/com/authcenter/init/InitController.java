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

package com.authcenter.init;

import com.authcenter.common.utils.CommonUtil;
import com.authcenter.common.utils.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现了 ApplicationRunner 接口的 InitController 类，用于初始化控制器.
 */
@RestController
public class InitController implements ApplicationRunner {
    /**
     * 服务https证书路径.
     */
    @Value("${server.ssl.key-store:}")
    private String serviceSsl;

    /**
     * 数据库证书路径.
     */
    @Value("${spring.datasource.casbin.hikari.data-source-properties.trustCertificateKeyStoreUrl:}")
    private String sqlSsl;

    private void deleteServiceSsl() {
        if (StringUtils.isBlank(serviceSsl)) {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file service ssl", "localhost", "failed,file not found");
            return;
        }
        if (CommonUtil.deleteFile(serviceSsl)) {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file service ssl", "localhost", "success");
        } else {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file service ssl", "localhost", "failed");
        }
    }

    private void deleteSqlSsl() {
        if (StringUtils.isBlank(sqlSsl)) {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file sql ssl", "localhost", "failed,file not found");
            return;
        }
        String[] splits = sqlSsl.split(":");
        String filePath = sqlSsl;
        if (splits.length == 2) {
            filePath = splits[1];
        }
        if (CommonUtil.deleteFile(filePath)) {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file sql ssl", "localhost", "success");
        } else {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file sql ssl", "localhost", "failed");
        }
    }

    private void deleteApplicationConfig() {
        String applicationPath = System.getenv("APPLICATION_PATH");
        if (StringUtils.isBlank(applicationPath)) {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file application.yaml", "localhost", "failed,file not found");
            return;
        }
        if (CommonUtil.deleteFile(applicationPath)) {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file application.yaml", "localhost", "success");
        } else {
            LogUtil.createLogs("system", "delete file", "application init",
                    "system delete file application.yaml", "localhost", "failed");
        }
    }

    /**
     * 运行应用程序的方法.
     *
     * @param args 应用程序参数
     * @throws Exception 可能抛出的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        deleteApplicationConfig();
        deleteServiceSsl();
        deleteSqlSsl();
    }
}
