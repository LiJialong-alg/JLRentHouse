package org.example.lease.web.admin.service;

import org.example.lease.web.admin.vo.login.CaptchaVo;
import org.example.lease.web.admin.vo.login.LoginVo;
import org.example.lease.web.admin.vo.system.user.SystemUserInfoVo;

public interface LoginService {

    CaptchaVo getCaptcha();

    String login(LoginVo loginVo);

    SystemUserInfoVo getUserInfoById(Long l);
}
