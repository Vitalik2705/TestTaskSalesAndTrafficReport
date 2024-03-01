package com.task.embedded;

import lombok.Data;

@Data
public class ReportSpecification {

    private String reportType;
    private ReportOptions reportOptions;
    private String dataStartTime;
    private String dataEndTime;
    private String[] marketplaceIds;
}

