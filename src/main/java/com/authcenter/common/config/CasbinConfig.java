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

package com.authcenter.common.config;

import lombok.Getter;
import lombok.Setter;
import org.casbin.adapter.JDBCAdapter;
import org.casbin.jcasbin.main.Enforcer;
import org.casbin.jcasbin.model.Model;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "enforcers")
@Getter
@Setter
public class CasbinConfig {
    /**
     * casbin实例.
     */
    private Map<String, EnforcerProperties> instances = new HashMap<>();

    /**
     * 生成实例.
     *
     * @param adapterFactory 实例工厂
     * @return 返回实例
     * @throws Exception 异常
     */
    @Bean
    public Map<String, Enforcer> enforcerMap(CasbinAdapterFactory adapterFactory) throws Exception {
        Map<String, Enforcer> enforcers = new HashMap<>();
        for (Map.Entry<String, EnforcerProperties> entry : instances.entrySet()) {
            JDBCAdapter adapter = adapterFactory.createAdapter(entry.getValue().getServiceName());
            ClassPathResource resource = new ClassPathResource(entry.getValue().getPolicyPath());
            String modelConfig = new String(
                    FileCopyUtils.copyToByteArray(resource.getInputStream()),
                    StandardCharsets.UTF_8
            );
            Model model = new Model();
            model.loadModelFromText(modelConfig);
            Enforcer enforcer = new Enforcer(model, adapter);
            enforcer.loadPolicy();
            enforcer.enableAutoSave(true);
            enforcers.put(entry.getKey(), enforcer);
        }
        return enforcers;
    }

    /**
     * 获取casbin实例.
     *
     * @return 返回实例
     */
    public Map<String, EnforcerProperties> getServiceInfo() {
        Map<String, EnforcerProperties> enforcerPropertiesMap = new HashMap<>();
        for (Map.Entry<String, EnforcerProperties> entry : instances.entrySet()) {
            enforcerPropertiesMap.put(entry.getValue().getServiceName(), entry.getValue());
        }
        return enforcerPropertiesMap;
    }
}
