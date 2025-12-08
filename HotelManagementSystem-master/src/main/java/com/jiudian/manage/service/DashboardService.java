package com.jiudian.manage.service;

import com.jiudian.manage.model.DashboardDTO;

public interface DashboardService {

    /**
     * 获取全局统计数据（带 Redis 缓存）
     */
    DashboardDTO getGlobalDashboard();
}
