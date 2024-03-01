package com.task.model;

import com.task.embedded.ReportSpecification;
import com.task.embedded.SalesAndTrafficByAsin;
import com.task.embedded.SalesAndTrafficByDate;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Document(collection = "maintable")
@Data
public class SalesAndTrafficReport {
    private @MongoId ObjectId id;
    private ReportSpecification reportSpecification;
    private List<SalesAndTrafficByDate> salesAndTrafficByDate;
    private List<SalesAndTrafficByAsin> salesAndTrafficByAsin;
}