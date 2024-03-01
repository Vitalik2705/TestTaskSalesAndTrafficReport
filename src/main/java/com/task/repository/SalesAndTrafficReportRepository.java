package com.task.repository;


import com.task.embedded.SalesAndTrafficByDate;
import com.task.model.SalesAndTrafficReport;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SalesAndTrafficReportRepository extends MongoRepository<SalesAndTrafficReport, String> {
}


