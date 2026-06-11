package org.example.lease.web.app.service.impl;

import org.example.lease.model.entity.*;
import org.example.lease.model.enums.ItemType;
import org.example.lease.web.app.mapper.*;
import org.example.lease.web.app.service.LeaseAgreementService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.lease.web.app.vo.agreement.AgreementDetailVo;
import org.example.lease.web.app.vo.agreement.AgreementItemVo;
import org.example.lease.web.app.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【lease_agreement(租约信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class LeaseAgreementServiceImpl extends ServiceImpl<LeaseAgreementMapper, LeaseAgreement>
        implements LeaseAgreementService {

    private static final Logger logger = LoggerFactory.getLogger(LeaseAgreementServiceImpl.class);

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;
    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private PaymentTypeMapper paymentTypeMapper;
    @Autowired
    private LeaseTermMapper leaseTermMapper;
    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Override
    public List<AgreementItemVo> listItemByPhone(String username) {
        return leaseAgreementMapper.listItemByPhone(username);
    }

    @Override
    public AgreementDetailVo getDetailById(Long id) {
        logger.info("getDetailById: 开始查询租约详情，id={}", id);

        // 1.查询租约信息
        LeaseAgreement leaseAgreement = leaseAgreementMapper.selectById(id);
        if (leaseAgreement == null) {
            logger.warn("getDetailById: 租约不存在，id={}", id);
            return null;
        }
        logger.info("getDetailById: 租约已找到，apartmentId={}, roomId={}, paymentTypeId={}, leaseTermId={}",
                leaseAgreement.getApartmentId(), leaseAgreement.getRoomId(),
                leaseAgreement.getPaymentTypeId(), leaseAgreement.getLeaseTermId());

        // 2.查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(leaseAgreement.getApartmentId());
        if (apartmentInfo == null) {
            logger.error("getDetailById: 公寓不存在，apartmentId={}", leaseAgreement.getApartmentId());
            return null;
        }

        // 3.查询房间信息
        RoomInfo roomInfo = roomInfoMapper.selectById(leaseAgreement.getRoomId());
        if (roomInfo == null) {
            logger.error("getDetailById: 房间不存在，roomId={}", leaseAgreement.getRoomId());
            return null;
        }

        // 4.查询图片信息
        List<GraphVo> roomGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM,
                leaseAgreement.getRoomId());
        List<GraphVo> apartmentGraphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT,
                leaseAgreement.getApartmentId());

        // 5.查询支付方式
        PaymentType paymentType = null;
        if (leaseAgreement.getPaymentTypeId() != null) {
            paymentType = paymentTypeMapper.selectById(leaseAgreement.getPaymentTypeId());
        }

        // 6.查询租期
        LeaseTerm leaseTerm = null;
        if (leaseAgreement.getLeaseTermId() != null) {
            leaseTerm = leaseTermMapper.selectById(leaseAgreement.getLeaseTermId());
        }

        AgreementDetailVo agreementDetailVo = new AgreementDetailVo();
        BeanUtils.copyProperties(leaseAgreement, agreementDetailVo);
        agreementDetailVo.setApartmentName(apartmentInfo.getName());
        agreementDetailVo.setRoomNumber(roomInfo.getRoomNumber());
        agreementDetailVo.setApartmentGraphVoList(apartmentGraphVoList);
        agreementDetailVo.setRoomGraphVoList(roomGraphVoList);
        if (paymentType != null) {
            agreementDetailVo.setPaymentTypeName(paymentType.getName());
        }
        if (leaseTerm != null) {
            agreementDetailVo.setLeaseTermMonthCount(leaseTerm.getMonthCount());
            agreementDetailVo.setLeaseTermUnit(leaseTerm.getUnit());
        }

        logger.info("getDetailById: 租约详情查询成功，返回数据");
        return agreementDetailVo;
    }

    @Override
    public List<PaymentType> listByRoomId(Long id) {
        return paymentTypeMapper.selectListByRoomId(id);
    }

    @Override
    public List<LeaseTerm> listByRoomId2(Long id) {
        return leaseTermMapper.selectListByRoomId(id);
    }
}
