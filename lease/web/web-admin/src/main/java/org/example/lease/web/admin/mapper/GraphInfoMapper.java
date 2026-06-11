package org.example.lease.web.admin.mapper;

import org.example.lease.model.entity.GraphInfo;
import org.example.lease.model.enums.ItemType;
import org.example.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author liubo
* @description 针对表【graph_info(图片信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity org.example.lease.model.GraphInfo
*/
public interface GraphInfoMapper extends BaseMapper<GraphInfo> {

    List<GraphVo> selectListById(ItemType itemType, Long id);
}




