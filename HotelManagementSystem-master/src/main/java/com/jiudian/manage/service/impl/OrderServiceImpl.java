package com.jiudian.manage.service.impl;

import com.github.pagehelper.PageHelper;
import com.jiudian.manage.mapper.ConfigMapper;
import com.jiudian.manage.mapper.OrderMapper;
import com.jiudian.manage.mapper.RoomMapper;
import com.jiudian.manage.model.Config;
import com.jiudian.manage.model.Order;
import com.jiudian.manage.model.Room;
import com.jiudian.manage.service.OrderService;
import com.jiudian.manage.until.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.jiudian.manage.service.RoomService;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    RoomMapper roomMapper;
    @Autowired
    ConfigMapper configMapper;

    @Autowired
    private RoomService roomService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OrderCoreService orderCoreService;// ✅ 注入刚刚那个带事务的核心 Service

    private static final String ROOM_LOCK_KEY_PREFIX = "lock:room:";




    @Override
    public boolean addOrder(String householdname, String id,
                            String starttime, String endtime,
                            int roomid, int userid) {

        String lockKey = ROOM_LOCK_KEY_PREFIX + roomid;
        String lockValue = UUID.randomUUID().toString();

        // 先加锁（Redis）
        Boolean success = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, lockValue);

        if (success == null || !success) {
            System.out.println("[OrderService] 房间 " + roomid + " 正在被其它请求处理，下单失败（未获得锁）");
            return false;
        }

// 2. 加锁成功后，再手动设置过期时间（比如 30 秒）
        stringRedisTemplate.expire(lockKey, 30, TimeUnit.SECONDS);

        try {
            boolean result = orderCoreService.addOrderCore(householdname, id, starttime, endtime, roomid, userid);
            System.out.println("[OrderService] 房间 " + roomid + " 正在进行下单处理，下单成功（已获得锁）");
            if (result) {
                // ✅ 重点：下单成功意味着房间变成了“已预订”，必须清理房间列表缓存！
                // 虽然 addOrderCore 可能会去改库，但为了保险，这里建议清一下
                roomService.clearRoomListCache();
            }
            return result;
        } finally {
            // 事务结束（方法返回）之后，再解锁
            unlockSafe(lockKey, lockValue);
        }
    }

    /**
     * 使用 Lua 脚本，保证“判断值 + 删除 key”是一个原子操作
     */
    private void unlockSafe(String key, String value) {
        String script =
                "if redis.call('get', KEYS[1]) == ARGV[1] then " +
                        "   return redis.call('del', KEYS[1]) " +
                        "else " +
                        "   return 0 " +
                        "end";
        stringRedisTemplate.execute(
                new DefaultRedisScript<>(script, Long.class),
                Collections.singletonList(key),
                value
        );
    }

    @Override
    public boolean delOrder(int orderid) {
        Order order = orderMapper.selectByPrimaryKey(orderid);
        if (order == null) {
            return false;
        }
        Integer roomid = order.getRoomid();
        Room room = new Room();
        room.setRoomid(roomid);
        room.setState(1);
        int i = roomMapper.updateByPrimaryKeySelective(room);
        if(i>0){
            int i1 = orderMapper.deleteByPrimaryKey(orderid);
            if(i1>0){
                roomService.clearRoomListCache();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateOrderState(int orderid, int state) {
        Order order = orderMapper.selectByPrimaryKey(orderid);
        if(order==null){
            return false;
        }
        Integer roomid = order.getRoomid();
        Room room = new Room();
        room.setRoomid(roomid);
        int i = 1;
        if(state==2){
            room.setState(3);
            i = roomMapper.updateByPrimaryKeySelective(room);
        }
        if(state==3){
            room.setState(1);
            i = roomMapper.updateByPrimaryKeySelective(room);
        }
        order.setState(state);
        if(i>0){
            int i1 = orderMapper.updateByPrimaryKeySelective(order);
            if(i1>0){
                roomService.clearRoomListCache();
                return true;
            }
        }
        return false;
    }

    @Override
    public List<Order> getAllOrder(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        return orderMapper.getAllUser();
    }
}
