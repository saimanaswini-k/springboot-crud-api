package com.example.entity;

import java.time.LocalDateTime;
import java.util.Arrays;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.PostLoad;
import jakarta.persistence.Transient;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "datasets")
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
    @JdbcTypeCode(SqlTypes.JSON)
    private String validationConfig;
    
    @Column(name = "extraction_config")
    @JdbcTypeCode(SqlTypes.JSON)
    private String extractionConfig;
    
    @Column(name = "dedup_config")
    @JdbcTypeCode(SqlTypes.JSON)
    private String dedupConfig;
    
    @Column(name = "data_schema")
    @JdbcTypeCode(SqlTypes.JSON)
    private String dataSchema;
    
    @Column(name = "denorm_config")
    @JdbcTypeCode(SqlTypes.JSON)
    private String denormConfig;
    
    @Column(name = "router_config")
    @JdbcTypeCode(SqlTypes.JSON)
    private String routerConfig;
    
    @Column(name = "dataset_config")
    @JdbcTypeCode(SqlTypes.JSON)
    private String datasetConfig;
    
    private String status;
    
    @Transient
    private String[] tags;
    
    @Column(name = "tags")
    private String tagsString;
    
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
    
    /**
     * Convert tags array to string before saving
     */
    @PrePersist
    @PreUpdate
    public void beforeSave() {
        if (tags != null && tags.length > 0) {
            // For PostgreSQL, convert the array to a proper SQL array string format
            StringBuilder sb = new StringBuilder("{");
            for (int i = 0; i < tags.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                // Escape quotes and add quotes around each element
                sb.append("\"").append(tags[i].replace("\"", "\\\"")).append("\"");
            }
            sb.append("}");
            tagsString = sb.toString();
        } else {
            tagsString = "{}";
        }
    }
    
    /**
     * Convert tags string to array after loading
     */
    @PostLoad
    public void afterLoad() {
        if (tagsString != null) {
            if (tagsString.isEmpty() || tagsString.equals("{}")) {
                tags = new String[0];
            } else {
                // For PostgreSQL array format like {"tag1","tag2"}
                if (tagsString.startsWith("{") && tagsString.endsWith("}")) {
                    String content = tagsString.substring(1, tagsString.length() - 1);
                    if (content.isEmpty()) {
                        tags = new String[0];
                    } else {
                        // Parse the PostgreSQL array format
                        java.util.List<String> tagList = new java.util.ArrayList<>();
                        boolean inQuotes = false;
                        StringBuilder currentTag = new StringBuilder();
                        
                        for (int i = 0; i < content.length(); i++) {
                            char c = content.charAt(i);
                            
                            if (c == '"') {
                                if (inQuotes && i + 1 < content.length() && content.charAt(i + 1) == '"') {
                                    // Double quote inside a quoted string - add as single quote
                                    currentTag.append('"');
                                    i++; // Skip the next quote
                                } else {
                                    // Toggle quote state
                                    inQuotes = !inQuotes;
                                }
                            } else if (c == ',' && !inQuotes) {
                                // End of current tag
                                tagList.add(currentTag.toString().trim());
                                currentTag = new StringBuilder();
                            } else {
                                currentTag.append(c);
                            }
                        }
                        
                        // Add the last tag if exists
                        if (currentTag.length() > 0) {
                            tagList.add(currentTag.toString().trim());
                        }
                        
                        // Convert to array and remove surrounding quotes from each tag
                        tags = tagList.stream()
                                .map(tag -> tag.trim().replaceAll("^\"|\"$", ""))
                                .toArray(String[]::new);
                    }
                } else {
                    // Fallback to comma-separated format if not a PostgreSQL array
                    tags = tagsString.split(",");
                }
            }
        } else {
            tags = new String[0];
        }
    }
}
