package org.example.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import org.apache.commons.codec.digest.DigestUtils;
import org.example.lease.common.constant.RedisConstant;
import org.example.lease.common.exception.LeaseException;
import org.example.lease.common.result.ResultCodeEnum;
import org.example.lease.common.utils.JWTUtil;
import org.example.lease.model.entity.SystemUser;
import org.example.lease.model.enums.BaseStatus;
import org.example.lease.web.admin.mapper.SystemUserMapper;
import org.example.lease.web.admin.service.LoginService;
import org.example.lease.web.admin.vo.login.CaptchaVo;
import org.example.lease.web.admin.vo.login.LoginVo;
import org.example.lease.web.admin.vo.system.user.SystemUserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SystemUserMapper systemUserMapper;

    @Override
    public CaptchaVo getCaptcha() {
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String verCode = captcha.text().toLowerCase();
        String uuid = RedisConstant.ADMIN_LOGIN_PREFIX + UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(uuid, verCode,RedisConstant.ADMIN_LOGIN_CAPTCHA_TTL_SEC, TimeUnit.SECONDS);
        return new CaptchaVo(captcha.toBase64(), uuid);
    }

    @Override
    public String login(LoginVo loginVo) {
        //校验验证码
        if(loginVo.getCaptchaCode() == null){
            throw new LeaseException(ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND.getCode(), ResultCodeEnum.ADMIN_CAPTCHA_CODE_NOT_FOUND.getMessage());

        }
        String code = stringRedisTemplate.opsForValue().get(loginVo.getCaptchaKey());
        if(code == null){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EXPIRED.getCode(), ResultCodeEnum.APP_LOGIN_CODE_EXPIRED.getMessage());

        }
        if(!code.equals(loginVo.getCaptchaCode().toLowerCase())){
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_ERROR.getCode(), ResultCodeEnum.APP_LOGIN_CODE_ERROR.getMessage());
        }
        //校验用户名密码
        SystemUser systemUser = systemUserMapper.selectOneByUsername(loginVo.getUsername());
        if(systemUser == null){
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR.getCode(), ResultCodeEnum.ADMIN_ACCOUNT_NOT_EXIST_ERROR.getMessage());
        }
        if(systemUser.getStatus() == BaseStatus.DISABLE)
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR.getCode(), ResultCodeEnum.ADMIN_ACCOUNT_DISABLED_ERROR.getMessage());
        if(!systemUser.getPassword().equals(DigestUtils.md5Hex(loginVo.getPassword())))
            throw new LeaseException(ResultCodeEnum.ADMIN_ACCOUNT_ERROR.getCode(), ResultCodeEnum.ADMIN_ACCOUNT_ERROR.getMessage());
        return JWTUtil.createJWT(systemUser.getId(), systemUser.getUsername());
    }

    @Override
    public SystemUserInfoVo getUserInfoById(Long userId) {
        SystemUser systemUser = systemUserMapper.selectById(userId);
        SystemUserInfoVo systemUserInfoVo = new SystemUserInfoVo();
        systemUserInfoVo.setName(systemUser.getName());
        systemUserInfoVo.setAvatarUrl(systemUser.getAvatarUrl());
        return systemUserInfoVo;
    }
}
