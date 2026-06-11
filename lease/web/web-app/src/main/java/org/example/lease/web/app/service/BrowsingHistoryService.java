package org.example.lease.web.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.lease.model.entity.BrowsingHistory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.lease.web.app.vo.history.HistoryItemVo;

/**
* @author liubo
* @description 针对表【browsing_history(浏览历史)】的数据库操作Service
* @createDate 2023-07-26 11:12:39
*/
public interface BrowsingHistoryService extends IService<BrowsingHistory> {
    IPage<HistoryItemVo> pageItemByUserId(Page<HistoryItemVo> historyItemVoPage, Long userId);

    void addHistory(Long userId, Long id);
}
