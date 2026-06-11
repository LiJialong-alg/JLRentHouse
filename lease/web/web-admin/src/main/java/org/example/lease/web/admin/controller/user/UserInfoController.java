package org.example.lease.web.admin.controller.user;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.lease.common.result.Result;
import org.example.lease.model.entity.UserInfo;
import org.example.lease.model.enums.BaseStatus;
import org.example.lease.web.admin.service.UserInfoService;
import org.example.lease.web.admin.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户信息管理")
@RestController
@RequestMapping("/admin/user")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;
    @Operation(summary = "分页查询用户信息")
    @GetMapping("page")
    public Result<IPage<UserInfo>> pageUserInfo(@RequestParam long current, @RequestParam long size, UserInfoQueryVo queryVo) {
        IPage<UserInfo> page = new Page<>(current, size);
        LambdaQueryWrapper<UserInfo> userInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userInfoLambdaQueryWrapper.like(queryVo.getPhone()!=null,UserInfo::getPhone, queryVo.getPhone())
                .eq(queryVo.getStatus()!=null,UserInfo::getStatus, queryVo.getStatus());
        IPage<UserInfo> page1 = userInfoService.page(page, userInfoLambdaQueryWrapper);

        return Result.ok(page1);
    }

    @Operation(summary = "根据用户id更新账号状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam BaseStatus status) {
        LambdaUpdateWrapper<UserInfo> userInfoLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        userInfoLambdaUpdateWrapper.eq(UserInfo::getId, id)
                .set(UserInfo::getStatus, status);
        userInfoService.update(userInfoLambdaUpdateWrapper);
        return Result.ok();
    }
}
