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

import com.authcenter.application.casbin.vo.PermissionRoleDetailsVO;
import com.authcenter.application.casbin.vo.PermissionRoleOnlyDomVO;
import com.authcenter.application.casbin.vo.PermissionRoleOnlyVO;
import com.authcenter.application.casbin.vo.ResourceVO;
import com.authcenter.application.casbin.vo.ServiceRoleVO;
import com.authcenter.common.config.CasbinConfig;
import com.authcenter.common.config.CommunityServiceConfig;
import com.authcenter.common.config.CommunityServiceProperties;
import com.authcenter.common.config.EnforcerProperties;
import com.authcenter.common.constant.CommonConstant;
import com.authcenter.common.constant.MessageCodeConstant;
import com.authcenter.common.utils.ResultUtil;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class UserAuthServiceImpl implements UserAuthService {
    /**
     * casbin上下文.
     */
    @Autowired
    private CasbinServiceContext casbinServiceContext;

    /**
     * casbin配置.
     */
    @Autowired
    private CasbinConfig casbinConfig;

    @Autowired
    private CommunityServiceConfig communityServiceConfig;

    /**
     * 是否有权限.
     *
     * @param service 服务
     * @param sub 用户ID
     * @param obj 资源对象
     * @param role 角色
     * @return 返回结果
     */
    @Override
    public ResponseEntity hasPermission(String service, String sub, String obj, String role) {
        HashMap<String, Boolean> checkData = new HashMap<>();
        checkData.put("hasPermission", false);
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.FORBIDDEN, MessageCodeConstant.E0002, null, checkData);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        boolean isAllowed = enforcerService.enforce(sub, obj, role);
        if (!isAllowed) {
            return ResultUtil.result(HttpStatus.FORBIDDEN, MessageCodeConstant.E0002, null, checkData);
        }
        checkData.put("hasPermission", true);
        return ResultUtil.result(HttpStatus.OK, "success", checkData);
    }

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
    @Override
    public ResponseEntity hasPermissionDom(String service, String sub, String obj, String role, String domain) {
        HashMap<String, Boolean> checkData = new HashMap<>();
        checkData.put("hasPermission", false);
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.FORBIDDEN, MessageCodeConstant.E0002, null, checkData);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        boolean isAllowed = enforcerService.enforce(sub, domain, obj, role);
        if (!isAllowed) {
            return ResultUtil.result(HttpStatus.FORBIDDEN, MessageCodeConstant.E0002, null, checkData);
        }
        checkData.put("hasPermission", true);
        return ResultUtil.result(HttpStatus.OK, "success", checkData);
    }

    /**
     * 是否有操作权限.
     *
     * @param service 服务
     * @param sub 用户ID
     * @param obj 资源对象
     * @param action 操作动作
     * @return 返回值
     */
    @Override
    public ResponseEntity hasPermissionAction(String service, String sub, String obj, String action) {
        HashMap<String, Boolean> checkData = new HashMap<>();
        checkData.put("hasPermission", false);
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.FORBIDDEN, MessageCodeConstant.E0002, null, checkData);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        boolean isAllowed = enforcerService.enforce(sub, obj, action);
        if (!isAllowed) {
            return ResultUtil.result(HttpStatus.FORBIDDEN, MessageCodeConstant.E0002, null, checkData);
        }
        checkData.put("hasPermission", true);
        return ResultUtil.result(HttpStatus.OK, "success", checkData);
    }

    /**
     * 获取权限信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @return 权限信息
     */
    @Override
    public ResponseEntity getPermissions(String service, String sub) {
        List<PermissionRoleDetailsVO> permissionRoleDetailsVOS = new ArrayList<>();
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", permissionRoleDetailsVOS);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);

        // 获取角色
        List<String> roles = enforcerService.getImplicitRolesForUser(sub);
        for (String role : roles) {
            List<List<String>> permissions = enforcerService.getImplicitPermissionsForUser(role);
            System.out.println(permissions.toArray());
        }
        return ResultUtil.result(HttpStatus.OK, "success", permissionRoleDetailsVOS);
    }

    /**
     * 获取仅角色的权限信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @param obj 资源对象
     * @return 权限信息
     */
    @Override
    public ResponseEntity getRoleOnlyPermissions(String service, String sub, String obj) {
        Set<String> permissionsForUser = new HashSet<>();
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", permissionsForUser);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        // 获取权限
        permissionsForUser = enforcerService.getPermittedActions(sub, obj);
        return ResultUtil.result(HttpStatus.OK, "success", permissionsForUser);
    }

    /**
     * 根据角色查询所有用户.
     *
     * @param service 服务
     * @param obj 资源
     * @param role 角色
     * @return 用户列表
     */
    @Override
    public ResponseEntity getRoleOnlyUsers(String service, String obj, String role) {
        Set<String> users = new HashSet<>();
        HashMap<String , Set<String>> userMap = new HashMap<>();
        userMap.put("userIds", users);
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", userMap);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        // 获取角色
        List<List<String>> policys = enforcerService.getPolicy();
        for (List<String> policy : policys) {
            if (obj.equals(policy.get(1)) && role.equals(policy.get(2))) {
                users.add(policy.get(0));
            }
        }
        return ResultUtil.result(HttpStatus.OK, "success", userMap);
    }

    /**
     * 获取角色详细信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @return 角色详情
     */
    @Override
    public ResponseEntity getRoleOnlyDetail(String service, String sub) {
        List<PermissionRoleOnlyVO> permissionActionVOS = new ArrayList<>();
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", permissionActionVOS);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        // 获取角色
        List<List<String>> permissionsForUser = enforcerService.getPermissionsForUser(sub);
        for (List<String> perf : permissionsForUser) {
            PermissionRoleOnlyVO permissionRoleOnlyVO = new PermissionRoleOnlyVO();
            permissionRoleOnlyVO.setObj(perf.get(1));
            permissionRoleOnlyVO.setRole(perf.get(2));
            permissionActionVOS.add(permissionRoleOnlyVO);
        }
        return ResultUtil.result(HttpStatus.OK, "success", permissionActionVOS);
    }

    @Override
    public ResponseEntity getDetailResource(String service, String subs) {
        Map<String, List<ResourceVO>> resourceMap = new HashMap<>();
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", resourceMap);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        List<String> subList = Arrays.stream(subs.split(",")).toList();
        for (String sub : subList) {
            List<List<String>> resources = enforcerService.getPermissionsForUser(sub);
            resourceMap.putIfAbsent(sub, new ArrayList<>());
            for (List<String> resource : resources) {
                ResourceVO resourceVO = new ResourceVO();
                resourceVO.setObj(resource.get(1));
                resourceVO.setType(resource.get(2));
                resourceMap.get(sub).add(resourceVO);
            }
        }
        return ResultUtil.result(HttpStatus.OK, "success", resourceMap);
    }

    /**
     * 获取带域的角色权限信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @param domain 域
     * @return 权限信息
     */
    @Override
    public ResponseEntity getRoleOnlyDomPermissions(String service, String sub, String domain) {
        List<PermissionRoleOnlyDomVO> permissionRoleOnlyDomVOS = new ArrayList<>();
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", permissionRoleOnlyDomVOS);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        // 获取角色
        List<List<String>> permissionsForUser = enforcerService.getPermissionsForUserInDomain(sub, domain);
        for (List<String> perf : permissionsForUser) {
            PermissionRoleOnlyDomVO permissionRoleOnlyDomVO = new PermissionRoleOnlyDomVO();
            permissionRoleOnlyDomVO.setDomain(perf.get(1));
            permissionRoleOnlyDomVO.setObj(perf.get(2));
            permissionRoleOnlyDomVO.setRole(perf.get(3));
            permissionRoleOnlyDomVOS.add(permissionRoleOnlyDomVO);
        }
        return ResultUtil.result(HttpStatus.OK, "success", permissionRoleOnlyDomVOS);
    }

    /**
     * 获取带域角色详细信息.
     *
     * @param service 服务ID
     * @param sub 用户ID
     * @return 角色详情
     */
    @Override
    public ResponseEntity getRoleOnlyDomDetail(String service, String sub) {
        List<PermissionRoleOnlyDomVO> permissionRoleOnlyDomVOS = new ArrayList<>();
        if (!casbinConfig.getServiceInfo().containsKey(service)) {
            return ResultUtil.result(HttpStatus.OK, "success", permissionRoleOnlyDomVOS);
        }
        Enforcer enforcerService = casbinServiceContext.getService(service);
        // 获取角色
        List<List<String>> permissionsForUser = enforcerService.getPermissionsForUser(sub);
        for (List<String> perf : permissionsForUser) {
            PermissionRoleOnlyDomVO permissionRoleOnlyDomVO = new PermissionRoleOnlyDomVO();
            permissionRoleOnlyDomVO.setDomain(perf.get(1));
            permissionRoleOnlyDomVO.setObj(perf.get(2));
            permissionRoleOnlyDomVO.setRole(perf.get(3));
            permissionRoleOnlyDomVOS.add(permissionRoleOnlyDomVO);
        }
        return ResultUtil.result(HttpStatus.OK, "success", permissionRoleOnlyDomVOS);
    }

    public ResponseEntity getRolesByCommunity(String community, String sub) {
        List<ServiceRoleVO> serviceRoleVOS = new ArrayList<>();
        CommunityServiceProperties serviceProperties = communityServiceConfig.getService(community);
        if (serviceProperties == null || CollectionUtils.isEmpty(serviceProperties.getServices())) {
            return ResultUtil.result(HttpStatus.OK, "success", serviceRoleVOS);
        }

        for (String service : serviceProperties.getServices()) {
            EnforcerProperties enforcerProperties = casbinConfig.getServiceInfo().get(service);
            ServiceRoleVO serviceRoleVO = new ServiceRoleVO();
            serviceRoleVO.setService(service);
            serviceRoleVO.setRoles(new HashSet<>());
            Enforcer enforcerService = casbinServiceContext.getService(service);
            // 获取角色
            List<List<String>> permissionsForUser = enforcerService.getPermissionsForUser(sub);
            for (List<String> perf : permissionsForUser) {
                switch (enforcerProperties.getAuthModelType()) {
                    case CommonConstant.AUTH_MODEL_TYPE_ROLE_ACTION:
                        serviceRoleVO.getRoles().add(perf.get(2));
                        break;
                    case CommonConstant.AUTH_MODEL_TYPE_ROLE_ONLY:
                        serviceRoleVO.getRoles().add(perf.get(2));
                        break;
                    case CommonConstant.AUTH_MODEL_TYPE_ROLE_ONLY_DOM:
                        serviceRoleVO.getRoles().add(perf.get(3));
                        break;
                    default:
                        break;
                }
            }
            if (serviceRoleVO.getRoles().size() > 0) {
                serviceRoleVOS.add(serviceRoleVO);
            }
        }

        return ResultUtil.result(HttpStatus.OK, "success", serviceRoleVOS);
    }
}
