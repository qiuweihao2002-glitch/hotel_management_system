package com.jiudian.manage.service.impl;

import com.jiudian.manage.mapper.ConfigMapper;
import com.jiudian.manage.mapper.OrderMapper;
import com.jiudian.manage.mapper.RoomMapper;
import com.jiudian.manage.model.Config;
import com.jiudian.manage.model.Order;
import com.jiudian.manage.model.Room;
import com.jiudian.manage.until.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderCoreService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private RoomMapper roomMapper;
    @Autowired
    private ConfigMapper configMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // Redis ZSet 的 key
    private static final String ORDER_DELAY_CANCEL_KEY = "order:delay:cancel";

    // 未支付超时时间（分钟）（方便测试写为一分钟）
    private static final long UNPAID_TIMEOUT_MINUTES = 1L;
    /**
     * 只负责“房间 + 订单 + 配置”的 DB 事务，不碰 Redis 锁
     */
    @Transactional
    public boolean addOrderCore(String householdname, String id,
                                String starttime, String endtime,
                                int roomid, int userid) {

        // 1. 查房间
        Room room = roomMapper.selectByPrimaryKey(roomid);
        if (room == null) {
            return false;
        }
        // 你原来的逻辑：state != 1 不允许下单
        if (room.getState() != 1) {
            return false;
        }

        // 2. 计算金额
        double money = TimeUtil.getBetweenDay(starttime, endtime) * room.getMoney();

        // 3. 组装订单
        Order order = new Order();
        order.setHouseholdname(householdname);
        order.setId(id);
        order.setStarttime(TimeUtil.formatterTime(starttime));
        order.setEndtime(TimeUtil.formatterTime(endtime));
        order.setRoomid(roomid);
        order.setUserid(userid);
        order.setState(0);
        order.setMoney(money);

        // 4. 原子更新全局统计（避免并发丢数据）
        configMapper.increaseStat(1, 1, money);

        // 5. 插入订单
        int insert = orderMapper.insertSelective(order);
        if (insert <= 0) {
            return false;
        }


        Integer orderId = order.getOrderid();
        if (orderId != null) {

            long executeAt = System.currentTimeMillis() + UNPAID_TIMEOUT_MINUTES * 60 * 1000L;
            try {
                stringRedisTemplate.opsForZSet()
                        .add(ORDER_DELAY_CANCEL_KEY, orderId.toString(), (double) executeAt);
                System.out.println("[DelayQueue] 注册订单自动取消任务，orderId=" + orderId);
            } catch (Exception e) {
                // 这里就算失败了，也只是“不会自动取消”，不影响下单本身
                e.printStackTrace();
            }
        }

        // 6. 将房间状态从 1 改为 2（已预订）
        Room room1 = new Room();
        room1.setRoomid(roomid);
        room1.setState(2);
        int i = roomMapper.updateByPrimaryKeySelective(room1);
        if (i <= 0) {
            // 这里抛异常，事务会整体回滚（订单 + Config + 房间状态都回滚）
            throw new RuntimeException("更新房间状态失败");
        }

        return true;
    }
}
