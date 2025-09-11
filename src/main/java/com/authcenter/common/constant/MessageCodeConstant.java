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

import java.util.HashMap;
import java.util.Map;

/**
 * 消息代码配置枚举类.
 */
public enum MessageCodeConstant {
    // success code message
    /**
     * 成功代码消息：一般成功.
     */
    S0001("S0001", "Success", "成功"),

    // fail code message
    /**
     * 错误代码 E0001: 验证码无效或已过期.
     */
    E0001("E0001", "Request Error", "请求异常"),

    /**
     * 错误代码 E0002: 验证码不正确.
     */
    E0002("E0002", "No permission.", "无权限访问"),

    /**
     * Error code E0003: Request exceeds the limit.
     * Chinese: 请求超过限制.
     */
    E0003("E0003", "Request exceeds the limit. Please try again later", "请求过于频繁，请稍后再次尝试"),

    /**
     * Error code E0004: Wrong parameter.
     * Chinese: 参数错误.
     */
    E0004("E0004", "Wrong parameter", "参数错误"),

    /**
     * 错误代码 E0005: 验证码token不正确.
     */
    E0005("E0005", "Verification token is not required.", "校验码不正确");

    /**
     * 消息代码.
     */
    private String code;

    /**
     * 英文消息.
     */
    private String msgEn;

    /**
     * 中文消息.
     */
    private String msgZh;


    MessageCodeConstant(String code, String msgEn, String msgZh) {
        this.code = code;
        this.msgEn = msgEn;
        this.msgZh = msgZh;
    }

    /**
     * 获取消息代码.
     *
     * @return 消息代码
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取英文消息.
     *
     * @return 英文消息
     */
    public String getMsgEn() {
        return msgEn;
    }

    /**
     * 获取中文消息.
     *
     * @return 中文消息
     */
    public String getMsgZh() {
        return msgZh;
    }

    /**
     * 获取错误代码映射.
     *
     * @return 包含错误代码配置对象的映射
     */
    public static Map<String, MessageCodeConstant> getErrorCode() {
        HashMap<String, MessageCodeConstant> map = new HashMap<>();
        map.put("Code invalid or expired", E0001);
        map.put("Wrong code. Try again.", E0002);

        return map;
    }
}
