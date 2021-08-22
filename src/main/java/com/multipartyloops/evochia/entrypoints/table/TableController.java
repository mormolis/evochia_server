package com.multipartyloops.evochia.entrypoints.table;

import com.multipartyloops.evochia.core.table.TableService;
import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import com.multipartyloops.evochia.entrypoints.table.dtos.AddTableRequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<TableInfoDto>> allTables(@RequestHeader Map<String, String> headers) {
        final var allTables = tableService.getAllTables();
        return new ResponseEntity<>(allTables, HttpStatus.OK);
    }

    @RequestMapping(value = "/table/id/{table_id}", method = RequestMethod.GET)
    public ResponseEntity<TableInfoDto> getTableById(@RequestHeader Map<String, String> headers, @PathVariable("table_id") String tableId) {
        final var table = tableService.getTableById(tableId);
        return new ResponseEntity<>(table, HttpStatus.OK);
    }

    @RequestMapping(value = "/table/alias/{table_alias}", method = RequestMethod.GET)
    public ResponseEntity<TableInfoDto> getTableByAlias(@RequestHeader Map<String, String> headers, @PathVariable("table_alias") String tableAlias) {
        final var table = tableService.getTableByAlias(tableAlias);
        return new ResponseEntity<>(table, HttpStatus.OK);
    }

    @RequestMapping(value = "/table/add", method = RequestMethod.POST)
    public ResponseEntity<TableInfoDto> addTable(@RequestHeader Map<String, String> headers, @RequestBody AddTableRequestBody body) {
        final var tableInfoDto = tableService.addTable(body.getTableGroupName(), body.getTableAlias(), body.getEnabled());
        return new ResponseEntity<>(tableInfoDto, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/table/delete/{table_alias}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteTable(@RequestHeader Map<String, String> headers, @PathVariable("table_alias") String tableAlias) {
        tableService.removeTable(tableAlias);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/table/disable/{table_alias}", method = RequestMethod.PUT)
    public ResponseEntity<Void> disableTable(@RequestHeader Map<String, String> headers, @PathVariable("table_alias") String tableAlias) {
        tableService.disableTable(tableAlias);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/table/enable/{table_alias}", method = RequestMethod.PUT)
    public ResponseEntity<Void> enableTable(@RequestHeader Map<String, String> headers, @PathVariable("table_alias") String tableAlias) {
        tableService.enableTable(tableAlias);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/table/group/enable/id/{group_id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> enableTableGroup(@RequestHeader Map<String, String> headers, @PathVariable("group_id") String tableGroupId) {
        tableService.enableTableGroup(tableGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/table/group/disable/id/{group_id}", method = RequestMethod.PUT)
    public ResponseEntity<Void> disableTableGroup(@RequestHeader Map<String, String> headers, @PathVariable("group_id") String tableGroupId) {
        tableService.disableTableGroup(tableGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
