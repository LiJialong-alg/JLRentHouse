package org.example.lease.web.app.controller.appointment;

import org.example.lease.common.login.LoginUserHolder;
import org.example.lease.common.result.Result;
import org.example.lease.model.entity.ViewAppointment;
import org.example.lease.model.enums.AppointmentStatus;
import org.example.lease.web.app.service.ViewAppointmentService;
import org.example.lease.web.app.vo.appointment.AppointmentDetailVo;
import org.example.lease.web.app.vo.appointment.AppointmentItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "看房预约信息")
@RestController
@RequestMapping("/app/appointment")
public class ViewAppointmentController {
    @Autowired
    private ViewAppointmentService viewAppointmentService;

    @Operation(summary = "保存或更新看房预约")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ViewAppointment viewAppointment) {
        viewAppointment.setUserId(LoginUserHolder.getLoginUser().getUserId());
        // 新增预约时设置默认状态为待看房
        if (viewAppointment.getAppointmentStatus() == null) {
            viewAppointment.setAppointmentStatus(AppointmentStatus.WAITING);
        }
        viewAppointmentService.saveOrUpdate(viewAppointment);
        return Result.ok();
    }

    @Operation(summary = "查询个人预约看房列表")
    @GetMapping("/listItem")
    public Result<List<AppointmentItemVo>> listItem() {
        List<AppointmentItemVo> appointmentItemVoList = viewAppointmentService
                .listItem(LoginUserHolder.getLoginUser().getUserId());
        return Result.ok(appointmentItemVoList);
    }

    @GetMapping("getDetailById")
    @Operation(summary = "根据ID查询预约详情信息")
    public Result<AppointmentDetailVo> getDetailById(Long id) {
        AppointmentDetailVo appointmentDetailVo = viewAppointmentService.getDetailById(id);
        return Result.ok(appointmentDetailVo);
    }

}
