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

import org.casbin.adapter.JDBCAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class CasbinAdapterFactory {
    /**
     * casbin数据源.
     */
    @Autowired
    @Qualifier("casbinDataSource")
    private DataSource dataSource;

    /**
     * 创建适配器.
     *
     * @param tableName 数据表名
     * @return 数据库适配器
     * @throws Exception 异常
     */
    public JDBCAdapter createAdapter(String tableName) throws Exception {
        return new JDBCAdapter(
                dataSource,
                false,
                tableName,
                true
        );
    }

    /**
     * 默认适配器.
     *
     * @return 默认适配器
     * @throws Exception 异常
     */
    public JDBCAdapter defaultAdapter() throws Exception {
        return createAdapter("casbin_rule");
    }
}
