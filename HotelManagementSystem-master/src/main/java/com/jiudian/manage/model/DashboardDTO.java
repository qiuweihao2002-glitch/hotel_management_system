package com.jiudian.manage.model;

public class DashboardDTO {
    // 累计订单数
    private Integer totalRoomOrderCount;

    // 累计营业额
    private Double totalMoney;

    // 预留一个字段，后面你愿意可以扩展（比如入住率）
    // private Double occupancyRate;

    public Integer getTotalRoomOrderCount() {
        return totalRoomOrderCount;
    }

    public void setTotalRoomOrderCount(Integer totalRoomOrderCount) {
        this.totalRoomOrderCount = totalRoomOrderCount;
    }

    public Double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(Double totalMoney) {
        this.totalMoney = totalMoney;
    }


}
