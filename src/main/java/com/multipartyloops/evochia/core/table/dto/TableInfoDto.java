package com.multipartyloops.evochia.core.table.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableInfoDto {
    private String tableId;
    private String tableAlias;
    private String groupId;
    private Boolean enabled;
}
