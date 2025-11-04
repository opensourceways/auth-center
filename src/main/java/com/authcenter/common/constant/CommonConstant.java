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

package com.authcenter.common.constant;

public class CommonConstant {
    /**
     * 电话号码正则表达式，匹配以加号开头的数字.
     */
    public static final String PHONEREGEX = "^(?:\\+?86)?1[3-9]\\d{9}$";
    /**
     * 邮箱正则表达式，匹配常见邮箱格式.
     */
    public static final String EMAILREGEX = "^[A-Za-z0-9-._\\u4e00-\\u9fa5]{1,40}"
            + "@[a-zA-Z0-9_-]{1,20}(\\.[a-zA-Z0-9_-]{1,20}){1,10}$";

    public static final String AUTH_MODEL_TYPE_ROLE_ACTION = "role_action";

    public static final String AUTH_MODEL_TYPE_ROLE_ONLY = "role_only";

    public static final String AUTH_MODEL_TYPE_ROLE_ONLY_DOM = "role_only_dom";
}
