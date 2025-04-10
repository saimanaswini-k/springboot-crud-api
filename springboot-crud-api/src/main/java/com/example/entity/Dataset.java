package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import com.vladmihalcea.hibernate.type.json.JsonType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "datasets")
@TypeDefs({
    @TypeDef(name = "json", typeClass = JsonType.class),
    @TypeDef(name = "string-array", typeClass = StringArrayType.class)
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dataset {

    @Id
    private String id;
    
    @Column(name = "dataset_id")
    private String datasetId;
    
    private String type;
    
    private String name;
    
    @Column(name = "validation_config")
    @Type(type = "json")
    private String validationConfig;
    
    @Column(name = "extraction_config")
    @Type(type = "json")
    private String extractionConfig;
    
    @Column(name = "dedup_config")
    @Type(type = "json")
    private String dedupConfig;
    
    @Column(name = "data_schema")
    @Type(type = "json")
    private String dataSchema;
    
    @Column(name = "denorm_config")
    @Type(type = "json")
    private String denormConfig;
    
    @Column(name = "router_config")
    @Type(type = "json")
    private String routerConfig;
    
    @Column(name = "dataset_config")
    @Type(type = "json")
    private String datasetConfig;
    
    private String status;
    
    @Type(type = "string-array")
    private String[] tags;
    
    @Column(name = "data_version")
    private Integer dataVersion;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "published_date")
    private LocalDateTime publishedDate;
}
