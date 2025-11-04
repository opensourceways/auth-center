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

import com.authcenter.aop.AuthingUserToken;
import com.authcenter.application.casbin.UserAuthService;
import jakarta.servlet.http.HttpServletRequest;
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

    @AuthingUserToken
    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public ResponseEntity getRoleOnly(HttpServletRequest servletRequest,
                                      @RequestParam("community") String community) {
        return userAuthService.getRolesByCommunity(community, servletRequest.getAttribute("username").toString());
    }
}
