package com.multipartyloops.evochia.entrypoints.table.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddTableRequestBody {

    @JsonProperty("tableGroupName")
    private String tableGroupName;

    @JsonProperty("tableAlias")
    private String tableAlias;

    @JsonProperty("enabled")
    private Boolean enabled;
}
