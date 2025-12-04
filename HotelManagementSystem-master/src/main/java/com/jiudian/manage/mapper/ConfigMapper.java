package com.jiudian.manage.mapper;

import com.jiudian.manage.model.Config;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Config record);

    int insertSelective(Config record);

    Config selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Config record);

    int updateByPrimaryKey(Config record);

    int increaseStat(@Param("id") Integer id,
                     @Param("roomDelta") int roomDelta,
                     @Param("moneyDelta") double moneyDelta);
}