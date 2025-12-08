package com.jiudian.manage.service.impl;


import com.jiudian.manage.mapper.OrderMapper;
import com.jiudian.manage.model.Order;
import com.jiudian.manage.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OrderDelayTaskService {

    private static final String ORDER_DELAY_CANCEL_KEY = "order:delay:cancel";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderService orderService;  // 复用你原来的 delOrder 逻辑

    /**
     * 每 60 秒扫描一次需要自动取消的订单
     */
    @Scheduled(fixedDelay = 10_000)
    public void scanAndCancelUnpaidOrders() {
        long now = System.currentTimeMillis();

        // 1. 从 ZSet 里拿出所有 score <= now 的订单ID（已经超时的）
        Set<String> orderIds = stringRedisTemplate.opsForZSet()
                .rangeByScore(ORDER_DELAY_CANCEL_KEY, 0, (double) now);

        if (orderIds == null || orderIds.isEmpty()) {
            return;
        }


        for (String orderIdStr : orderIds) {
            try {
                int orderId = Integer.parseInt(orderIdStr);

                Order order = orderMapper.selectByPrimaryKey(orderId);
                if (order == null) {
                    // 订单都没了，直接清理 ZSet 中的记录
                    stringRedisTemplate.opsForZSet().remove(ORDER_DELAY_CANCEL_KEY, orderIdStr);
                    continue;
                }

                // 仅当订单仍然是“未支付/未完成”（state == 0）时才取消
                if (order.getState() != null && order.getState() == 0) {
                    boolean success = orderService.delOrder(orderId);
                    if (success) {
                        System.out.println("[DelayCancel] 自动取消未支付订单，orderId=" + orderId);
                    } else {
                        System.out.println("[DelayCancel] 取消订单失败，orderId=" + orderId);
                    }
                }

                // 无论取消是否成功，先把这个任务从 ZSet 里删掉，避免反复处理
                stringRedisTemplate.opsForZSet().remove(ORDER_DELAY_CANCEL_KEY, orderIdStr);

            } catch (Exception e) {
                e.printStackTrace();
                // 某个订单处理失败，不影响其他订单
            }
        }
    }
}
