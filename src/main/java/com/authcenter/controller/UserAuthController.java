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

import com.authcenter.application.casbin.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/auth")
@RestController
public class UserAuthController {
    @Autowired
    private UserAuthService userAuthService;

    @RequestMapping(value = "/check/roleOnly", method = RequestMethod.GET)
    public ResponseEntity checkRoleOnly(@RequestParam("service") String service, @RequestParam("sub") String sub,
                                          @RequestParam("obj") String obj, @RequestParam("role") String role) {
        return userAuthService.hasPermission(service, sub, obj, role);
    }

    @RequestMapping(value = "/check/roleOnlyDom", method = RequestMethod.GET)
    public ResponseEntity checkRoleOnlyDom(@RequestParam("service") String service, @RequestParam("sub") String sub,
                                        @RequestParam("obj") String obj, @RequestParam("role") String role,
                                        @RequestParam("domain") String domain) {
        return userAuthService.hasPermissionDom(service, sub, obj, role, domain);
    }

    @RequestMapping(value = "/check/roleAction", method = RequestMethod.GET)
    public ResponseEntity checkRoleAction(@RequestParam("service") String service, @RequestParam("sub") String sub,
                                          @RequestParam("obj") String obj, @RequestParam("action") String action) {
        return userAuthService.hasPermissionAction(service, sub, obj, action);
    }

    @RequestMapping(value = "/get/detail/roleActions", method = RequestMethod.GET)
    public ResponseEntity getRoleActions(@RequestParam("service") String service, @RequestParam("sub") String sub) {
        return userAuthService.getPermissions(service, sub);
    }

    @RequestMapping(value = "/get/detail/roleOnly", method = RequestMethod.GET)
    public ResponseEntity getRoleOnly(@RequestParam("service") String service, @RequestParam("sub") String sub) {
        return userAuthService.getRoleOnlyDetail(service, sub);
    }

    @RequestMapping(value = "/get/permission/roleOnly", method = RequestMethod.GET)
    public ResponseEntity getRoleOnly(@RequestParam("service") String service, @RequestParam("sub") String sub,
                                        @RequestParam("obj") String obj) {
        return userAuthService.getRoleOnlyPermissions(service, sub, obj);
    }

    @RequestMapping(value = "/get/detail/roleOnlyDom", method = RequestMethod.GET)
    public ResponseEntity getRoleOnlyDom(@RequestParam("service") String service, @RequestParam("sub") String sub) {
        return userAuthService.getRoleOnlyDomDetail(service, sub);
    }

    @RequestMapping(value = "/get/permission/roleOnlyDom", method = RequestMethod.GET)
    public ResponseEntity getRoleOnlyDom(@RequestParam("service") String service, @RequestParam("sub") String sub,
                                      @RequestParam("domain") String domain) {
        return userAuthService.getRoleOnlyDomPermissions(service, sub, domain);
    }
}
