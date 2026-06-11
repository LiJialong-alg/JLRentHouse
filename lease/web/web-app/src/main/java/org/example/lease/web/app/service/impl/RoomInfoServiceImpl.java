package org.example.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.lease.common.login.LoginUserHolder;
import org.example.lease.model.entity.*;
import org.example.lease.model.enums.ItemType;
import org.example.lease.web.app.mapper.*;
import org.example.lease.web.app.service.ApartmentInfoService;
import org.example.lease.web.app.service.BrowsingHistoryService;
import org.example.lease.web.app.service.GraphInfoService;
import org.example.lease.web.app.service.RoomInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.lease.web.app.vo.apartment.ApartmentItemVo;
import org.example.lease.web.app.vo.attr.AttrValueVo;
import org.example.lease.web.app.vo.fee.FeeValueVo;
import org.example.lease.web.app.vo.graph.GraphVo;
import org.example.lease.web.app.vo.room.RoomDetailVo;
import org.example.lease.web.app.vo.room.RoomItemVo;
import org.example.lease.web.app.vo.room.RoomQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
@Slf4j
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    //给所有mapper定义
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private FeeValueMapper feeValueMapper;
    @Autowired
    private LeaseTermMapper leaseTermMapper;
    @Autowired
    private PaymentTypeMapper paymentTypeMapper;
    @Autowired
    private ApartmentInfoService apartmentInfoService;
    @Autowired
    private RoomInfoMapper roomInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;
    @Autowired
    private BrowsingHistoryService browsingHistoryService;
    @Override
    public IPage<RoomItemVo> pageItem(Page<RoomItemVo> roomItemVoPage, RoomQueryVo queryVo) {
        return roomInfoMapper.pageItem(roomItemVoPage, queryVo);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        if (roomInfo == null) {
            return null;
        }
        //2.查询图片
        List<GraphVo> graphVoList = graphInfoMapper.selectListByItemTypeAndId(ItemType.ROOM, id);
        //3.查询租期
        List<LeaseTerm> leaseTermList = leaseTermMapper.selectListByRoomId(id);
        //4.查询配套
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListByRoomId(id);
        //5.查询标签
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListByRoomId(id);
        //6.查询支付方式
        List<PaymentType> paymentTypeList = paymentTypeMapper.selectListByRoomId(id);
        //7.查询基本属性
        List<AttrValueVo> attrValueVoList = attrValueMapper.selectListByRoomId(id);
        //8.查询杂费信息
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListByApartmentId(roomInfo.getApartmentId());
        //9.查询公寓信息
        ApartmentItemVo apartmentItemVo = apartmentInfoService.selectApartmentItemVoById(roomInfo.getApartmentId());

        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, roomDetailVo);

        roomDetailVo.setApartmentItemVo(apartmentItemVo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setFeeValueVoList(feeValueVoList);
        roomDetailVo.setLeaseTermList(leaseTermList);


        browsingHistoryService.addHistory(LoginUserHolder.getLoginUser().getUserId(),id);
        return roomDetailVo;
    }

    @Override
    public IPage<RoomItemVo> pageItemByApartmentId(Page<RoomItemVo> roomItemVoPage, Long id) {
        return roomInfoMapper.pageItemByApartmentId(roomItemVoPage, id);
    }
}




