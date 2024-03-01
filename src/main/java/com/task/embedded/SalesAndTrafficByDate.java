package com.task.embedded;

import lombok.Data;

@Data
public class SalesAndTrafficByDate {

    private String date;
    private SalesByDate salesByDate;
    private TrafficByDate trafficByDate;

}
