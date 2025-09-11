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

package com.authcenter.controller;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonAPIControllerTest {
    /**
     * CommonAPIController实例.
     */
    private CommonAPIController commonAPIController = new CommonAPIController();

    /**
     * 检查服务健康状态.
     */
    @Test
    public void checkOmService() {
        assertThat(commonAPIController.checkOmService()).isEqualTo("normal");
    }
}