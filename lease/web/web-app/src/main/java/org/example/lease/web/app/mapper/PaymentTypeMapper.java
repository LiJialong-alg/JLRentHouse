package org.example.lease.web.app.mapper;

import org.example.lease.model.entity.PaymentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【payment_type(支付方式表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity org.example.lease.model.entity.PaymentType
*/
public interface PaymentTypeMapper extends BaseMapper<PaymentType> {

    List<PaymentType> selectListByRoomId(Long id);
}




