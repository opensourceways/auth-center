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

@Setter
@Getter
public class EnforcerProperties {
    /**
     * 模型类型.
     */
    private String authModelType;

    /**
     * 服务名称.
     */
    private String serviceName;

    /**
     * 策略文件.
     */
    private String policyPath;
}
