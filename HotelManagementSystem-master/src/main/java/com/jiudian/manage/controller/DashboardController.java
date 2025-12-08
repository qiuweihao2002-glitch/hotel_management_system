package com.jiudian.manage.controller;

import com.jiudian.manage.model.DashboardDTO;

import com.jiudian.manage.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping("/global.do")
    public DashboardDTO getGlobalDashboard() {
        return dashboardService.getGlobalDashboard();
    }
}