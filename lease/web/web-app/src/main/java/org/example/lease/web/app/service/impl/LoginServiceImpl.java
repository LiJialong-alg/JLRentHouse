package org.example.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.lease.common.exception.LeaseException;
import org.example.lease.common.result.ResultCodeEnum;
import org.example.lease.common.utils.JWTUtil;
import org.example.lease.model.entity.UserInfo;
import org.example.lease.model.enums.BaseStatus;
import org.example.lease.web.app.mapper.UserInfoMapper;
import org.example.lease.web.app.service.LoginService;
import org.example.lease.web.app.service.UserInfoService;
import org.example.lease.web.app.vo.user.LoginVo;
import org.example.lease.web.app.vo.user.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public void getCode(String phone) {

    }

    @Override
    public String login(LoginVo loginVo) {
        if (!StringUtils.hasText(loginVo.getPhone())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_PHONE_EMPTY.getCode(),ResultCodeEnum.APP_LOGIN_PHONE_EMPTY.getMessage());
        }

        if (!StringUtils.hasText(loginVo.getCode())) {
            throw new LeaseException(ResultCodeEnum.APP_LOGIN_CODE_EMPTY.getCode(),ResultCodeEnum.APP_LOGIN_CODE_EMPTY.getMessage());
        }
        //2.校验验证码,省略
        //3.判断用户是否存在,不存在则注册（创建用户）
        LambdaQueryWrapper<UserInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getPhone, loginVo.getPhone());
        UserInfo userInfo = userInfoService.getOne(queryWrapper);
        if (userInfo == null) {
            userInfo = new UserInfo();
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(BaseStatus.ENABLE);
            userInfo.setNickname("用户-"+loginVo.getPhone().substring(7));
            userInfoService.save(userInfo);
        }

        //4.判断用户是否被禁
        if (userInfo.getStatus().equals(BaseStatus.DISABLE)) {
            throw new LeaseException(ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR.getCode(),ResultCodeEnum.APP_ACCOUNT_DISABLED_ERROR.getMessage());
        }

        //5.创建并返回TOKEN
        return JWTUtil.createJWT(userInfo.getId(), loginVo.getPhone());

    }

    @Override
    public UserInfoVo getUserInfoById(Long userId) {
        UserInfo userInfo = userInfoMapper.selectById(userId);
        UserInfoVo userInfoVo  = new UserInfoVo(userInfo.getNickname(),userInfo.getAvatarUrl());
        return userInfoVo;

    }
}
