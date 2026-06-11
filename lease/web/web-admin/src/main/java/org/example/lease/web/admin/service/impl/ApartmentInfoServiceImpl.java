package org.example.lease.web.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.lease.common.exception.LeaseException;
import org.example.lease.common.result.ResultCodeEnum;
import org.example.lease.model.entity.*;
import org.example.lease.model.enums.ItemType;
import org.example.lease.web.admin.mapper.*;
import org.example.lease.web.admin.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.lease.web.admin.vo.apartment.ApartmentDetailVo;
import org.example.lease.web.admin.vo.apartment.ApartmentItemVo;
import org.example.lease.web.admin.vo.apartment.ApartmentQueryVo;
import org.example.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import org.example.lease.web.admin.vo.fee.FeeValueVo;
import org.example.lease.web.admin.vo.graph.GraphVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {
    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private FeeValueMapper feeValueMapper;
    @Autowired
    private GraphInfoMapper graphInfoMapper;
    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private GraphInfoService graphInfoService;
    @Autowired
    private ApartmentFacilityService apartmentFacilityService;
    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Override
    public void saveOrUpdateApartmentInfo(ApartmentSubmitVo apartmentSubmitVo) {
        System.out.println(apartmentSubmitVo);
        Boolean isUpdate = apartmentSubmitVo.getId() != null;
        super.saveOrUpdate(apartmentSubmitVo);
        if (isUpdate) {
            //删除图片列表
            LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
            graphInfoQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphInfoQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphInfoQueryWrapper);
            //删除配套列表
            LambdaQueryWrapper<ApartmentFacility> apartmentFacilityWrapper = new LambdaQueryWrapper<>();
            apartmentFacilityWrapper.eq(ApartmentFacility::getApartmentId, apartmentSubmitVo.getId());
            apartmentFacilityService.remove(apartmentFacilityWrapper);

            //删除标签列表
            LambdaQueryWrapper<ApartmentLabel> apartmentLabelWrapper = new LambdaQueryWrapper<>();
            apartmentLabelWrapper.eq(ApartmentLabel::getApartmentId, apartmentSubmitVo.getId());
            apartmentLabelService.remove(apartmentLabelWrapper);

            //删除杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueWrapper = new LambdaQueryWrapper<>();
            apartmentFeeValueWrapper.eq(ApartmentFeeValue::getApartmentId, apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(apartmentFeeValueWrapper);
        }
        // 新增图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if(!CollectionUtils.isEmpty(graphVoList)){
            ArrayList<GraphInfo> graphInfos = new ArrayList<>();
            for (GraphVo graphVo : graphVoList) {
                GraphInfo graphInfo = new GraphInfo();
                graphInfo.setName(graphVo.getName());
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfos.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfos);
        }
        // 新增配套列表
        List<Long> facilityInfoIds = apartmentSubmitVo.getFacilityInfoIds();
        if(!CollectionUtils.isEmpty(facilityInfoIds)){
            ArrayList<ApartmentFacility> apartmentFacilities = new ArrayList<>();
            for (Long facilityInfoId : facilityInfoIds) {
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(facilityInfoId);
                apartmentFacilities.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(apartmentFacilities);
        }

        //3.插入标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if (!CollectionUtils.isEmpty(labelIds)) {
            List<ApartmentLabel> apartmentLabelList = new ArrayList<>();
            for (Long labelId : labelIds) {
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(labelId);
                apartmentLabelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabelList);
        }


        //4.插入杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if (!CollectionUtils.isEmpty(feeValueIds)) {
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for (Long feeValueId : feeValueIds) {
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(feeValueId);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList);
        }

    }

    @Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> apartmentItemVoPage, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageItem(apartmentItemVoPage, queryVo);
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //查询公寓信息
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        //查询图片列表
        List<GraphVo> graphVoList = graphInfoMapper.selectListById(ItemType.APARTMENT,id);

        //查询标签列表
        List<LabelInfo> labelInfoList = labelInfoMapper.selectListById(id);
        //查询配套列表
        List<FacilityInfo> facilityInfoList = facilityInfoMapper.selectListById(id);
        //查询杂费列表
        List<FeeValueVo> feeValueVoList = feeValueMapper.selectListById(id);

        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo, apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);


        return apartmentDetailVo;
    }

    @Override
    public void removeApartmentById(Long id) {
        LambdaQueryWrapper<RoomInfo> roomInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoLambdaQueryWrapper.eq(RoomInfo::getApartmentId, id);
        Long l = roomInfoMapper.selectCount(roomInfoLambdaQueryWrapper);
        if (l > 0) {
            throw new LeaseException(ResultCodeEnum.DELETE_ERROR.getCode(), "该公寓下有房间，请先删除该公寓下的房间！");
        }
        super.removeById(id);
        //删除图片列表
        LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphInfoQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphInfoQueryWrapper);
        //删除配套列表
        LambdaQueryWrapper<ApartmentFacility> apartmentFacilityWrapper = new LambdaQueryWrapper<>();
        apartmentFacilityWrapper.eq(ApartmentFacility::getApartmentId, id);
        apartmentFacilityService.remove(apartmentFacilityWrapper);

        //删除标签列表
        LambdaQueryWrapper<ApartmentLabel> apartmentLabelWrapper = new LambdaQueryWrapper<>();
        apartmentLabelWrapper.eq(ApartmentLabel::getApartmentId, id);
        apartmentLabelService.remove(apartmentLabelWrapper);

        //删除杂费列表
        LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueWrapper = new LambdaQueryWrapper<>();
        apartmentFeeValueWrapper.eq(ApartmentFeeValue::getApartmentId, id);
        apartmentFeeValueService.remove(apartmentFeeValueWrapper);
    }
}




