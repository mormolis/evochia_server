package com.multipartyloops.evochia.core.table.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableGroupingDto {
    private String groupId;
    private String groupName;
    private Boolean enabled;
}
