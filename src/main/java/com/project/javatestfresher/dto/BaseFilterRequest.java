package com.project.salebe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.project.salebe.enums.ErrorCode;
import com.project.salebe.exceptions.SaleappException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Data
public class BaseFilterRequest {

    private Integer page;
    private Integer size;
    private String sortBy = "id";
    private String sortDir = "desc";
    private String sortBy2;
    private String sortDir2;
    private String keyword;
    private String value;
    
    @JsonIgnore
    private Boolean hasSortBy() {
        return StringUtils.isNotEmpty(sortBy) && StringUtils.isNotEmpty(sortDir);
    }
    
    @JsonIgnore
    private Boolean hasSortBy2() {
        return StringUtils.isNotEmpty(sortBy2) && StringUtils.isNotEmpty(sortDir2);
    }
    
    @JsonIgnore
    private Sort getSort() {
        if (!hasSortBy()) {
            return Sort.by(Sort.Direction.DESC, "id");
        }
        
        Sort sort = Sort.by("asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        
        if (hasSortBy2()) {
            Sort sort2 = Sort.by("asc".equalsIgnoreCase(sortDir2) ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy2);
            sort.and(sort2);
        }
        
        return sort;
    }
    
    @JsonIgnore
    private String getOrderBy() {
        if (!hasSortBy()) {
            return "";
        }
        
        if (StringUtils.containsWhitespace(sortBy) || StringUtils.containsWhitespace(sortDir)) { // Avoid SQL injection
            throw new SaleappException(ErrorCode.OTHER, "Invalid sorting characters");
        }
        
        String ret = "order by " +  sortBy + " " + sortDir;
        
        if (hasSortBy2()) {
            if (StringUtils.containsWhitespace(sortBy2) || StringUtils.containsWhitespace(sortDir2)) { // Avoid SQL injection
                throw new SaleappException(ErrorCode.OTHER, "Invalid sorting characters");
            }
            
            ret += ", " + sortBy2 + " " + sortDir2;
        }
        return ret;
    }
    
    @JsonIgnore
    public String getOrderByOffsetLimit() {
        Integer offset = getPageOrDefault() * getSizeOrDefault();
        String ret = getOrderBy() + " limit " + offset + "," + getSizeOrDefault();
        return ret;
    }
    
    @JsonIgnore
    private Integer getSizeOrDefault() {
        return size != null ? size : 10000;
    }
    
    @JsonIgnore
    private Integer getPageOrDefault() {
        return page != null ? page : 0;
    }
    
    @JsonIgnore
    public Pageable getPageable() {
        return PageRequest.of(getPageOrDefault(), getSizeOrDefault(), getSort());
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}