package com.project.javatestfresher.dto.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class ToolError {
    private String name;
    @JsonProperty("number_id")
    private String numberId;
    @JsonProperty("tool_type_id")
    private String toolTypeId;
    @JsonProperty("supplier_id")
    private String supplierId;
    private int status;
    private int condition;
    private String unit;
    private String note;
    private List<MultipartFile> image;
    @JsonProperty("child_details")
    private List<String> childDetails;
    private String description;
    @JsonProperty("location_id")
    private String locationId;
    @JsonProperty("po_number")
    private String poNumber;
    @JsonProperty("invoice_number")
    private String invoiceNumber;
    @JsonProperty("arrival_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date arrivalDate;
    private Integer type;
    private String message;
}
