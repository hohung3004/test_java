package com.project.javatestfresher.dto.user.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchUseresponse {
    @JsonProperty("record_size")
    private Long recordSize;
    private List<AdminResponse> list;
}
