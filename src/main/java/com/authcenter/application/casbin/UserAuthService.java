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

package com.authcenter.application.casbin;

import org.springframework.http.ResponseEntity;

public interface UserAuthService {
    /**
     * 是否有权限.
     *
     * @param service 服务
     * @param sub 用户ID
     * @param obj 资源对象
     * @param role 角色
     * @return 返回结果
     */
    ResponseEntity hasPermission(String service, String sub, String obj, String role);

    /**
     * 是否有权限(租户领域).
     *
     * @param service 服务
     * @param sub 用户ID
     * @param obj 资源对象
     * @param role 角色
     * @param domain 域
     * @return 返回结果
     */
    ResponseEntity hasPermissionDom(String service, String sub, String obj, String role, String domain);

    /**
     * 是否有操作权限.
     *
     * @param service 服务
     * @param sub 用户ID
     * @param obj 资源对象
     * @param action 操作动作
     * @return 返回值
     */
    ResponseEntity hasPermissionAction(String service, String sub, String obj, String action);

    /**
     * 获取权限信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @return 权限信息
     */
    ResponseEntity getPermissions(String service, String sub);

    /**
     * 获取仅角色的权限信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @param obj 资源对象
     * @return 权限信息
     */
    ResponseEntity getRoleOnlyPermissions(String service, String sub, String obj);

    /**
     * 根据角色查询所有用户.
     *
     * @param service 服务
     * @param obj 资源
     * @param role 角色
     * @return 用户列表
     */
    ResponseEntity getRoleOnlyUsers(String service, String obj, String role);

    /**
     * 获取带域的角色权限信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @param domain 域
     * @return 权限信息
     */
    ResponseEntity getRoleOnlyDomPermissions(String service, String sub, String domain);

    /**
     * 获取角色详细信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @return 角色详情
     */
    ResponseEntity getRoleOnlyDetail(String service, String sub);

    /**
     * 获取带域角色详细信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @return 角色详情
     */
    ResponseEntity getRoleOnlyDomDetail(String service, String sub);

    /**
     * 按社区获取角色信息.
     *
     * @param community 社区
     * @param sub 用户
     * @return 角色信息
     */
    ResponseEntity getRolesByCommunity(String community, String sub);
}
