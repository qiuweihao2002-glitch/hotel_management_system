package com.jiudian.manage.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jiudian.manage.model.DashboardDTO;
import com.jiudian.manage.mapper.ConfigMapper;
import com.jiudian.manage.model.Config;
import com.jiudian.manage.model.DashboardDTO;
import com.jiudian.manage.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String DASHBOARD_CACHE_KEY = "dashboard:global";

    @Override
    public com.jiudian.manage.model.DashboardDTO getGlobalDashboard() {
        // 1. 先尝试从 Redis 里读缓存
        try {
            String json = stringRedisTemplate.opsForValue().get(DASHBOARD_CACHE_KEY);
            if (StringUtils.hasText(json)) {
                DashboardDTO dto = objectMapper.readValue(json, DashboardDTO.class);
                System.out.println("[Dashboard] 命中 Redis 缓存");
                return dto;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 缓存没有命中：从数据库查 config 表
        Config config = configMapper.selectByPrimaryKey(1);

        DashboardDTO dto = new DashboardDTO();
        if (config != null) {
            dto.setTotalRoomOrderCount(((Double) config.getTotalroom()).intValue());
            dto.setTotalMoney(config.getTotalmoney());
        } else {
            dto.setTotalRoomOrderCount(0);
            dto.setTotalMoney(0.0);
        }

        // 3. 把结果写入 Redis，设置一个比较短的过期时间（例如 60 秒）
        try {
            String json = objectMapper.writeValueAsString(dto);
            stringRedisTemplate
                    .opsForValue()
                    .set(DASHBOARD_CACHE_KEY, json, 60, TimeUnit.SECONDS);
            System.out.println("[Dashboard] 写入 Redis 缓存");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dto;
    }

    /**
     * 预留一个清缓存的方法，有需要时可调用
     */
    public void clearDashboardCache() {
        stringRedisTemplate.delete(DASHBOARD_CACHE_KEY);
    }
}

