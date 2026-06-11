package org.example.lease.web.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.lease.model.entity.BrowsingHistory;
import org.example.lease.web.app.mapper.BrowsingHistoryMapper;
import org.example.lease.web.app.service.BrowsingHistoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.lease.web.app.vo.history.HistoryItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liubo
 * @description 针对表【browsing_history(浏览历史)】的数据库操作Service实现
 * @createDate 2023-07-26 11:12:39
 */
@Service
public class BrowsingHistoryServiceImpl extends ServiceImpl<BrowsingHistoryMapper, BrowsingHistory>
        implements BrowsingHistoryService {
    @Autowired
    private BrowsingHistoryMapper browsingHistoryMapper;
    @Override
    public IPage<HistoryItemVo> pageItemByUserId(Page<HistoryItemVo> historyItemVoPage, Long userId) {

        return browsingHistoryMapper.pageItemByUserId(historyItemVoPage, userId);
    }

    @Override
    @Async
    public void addHistory(Long userId, Long id) {
        LambdaQueryWrapper<BrowsingHistory> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        objectLambdaQueryWrapper.eq(BrowsingHistory::getUserId, userId)
                .eq(BrowsingHistory::getRoomId, id);
        BrowsingHistory browsingHistory = browsingHistoryMapper.selectOne(objectLambdaQueryWrapper);
        if(browsingHistory == null){
            BrowsingHistory History = new BrowsingHistory();
            History.setUserId(userId);
            History.setRoomId(id);
            History.setBrowseTime(new Date());
            browsingHistoryMapper.insert(History);

        }else {
            browsingHistory.setBrowseTime(new Date());
            browsingHistoryMapper.updateById(browsingHistory);
        }


    }
}