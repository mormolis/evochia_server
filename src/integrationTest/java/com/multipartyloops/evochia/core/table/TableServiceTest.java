package com.multipartyloops.evochia.core.table;

import com.multipartyloops.evochia.core.table.dto.TableGroupingDto;
import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import com.multipartyloops.evochia.persistance.table.TableInfoRepository;
import com.multipartyloops.evochia.persistance.table.TableJDBCTest;
import com.multipartyloops.evochia.persistance.table.grouping.TableGroupingJDBCRepository;
import com.multipartyloops.evochia.persistance.table.grouping.TableGroupingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
public class TableServiceTest extends TableJDBCTest {

    @Mock
    private TableInfoRepository<TableInfoDto> tableInfoRepositoryMock;

    private TableGroupingRepository<TableGroupingDto> tableGroupingRepository;
    private TransactionTemplate transactionTemplate;
    private TableService tableService;

    @BeforeEach
    void setup() {
        tableGroupingRepository = new TableGroupingJDBCRepository(jdbcTemplate, uuidPersistenceTransformer);
        transactionTemplate = new TransactionTemplate(new DataSourceTransactionManager(dataSource));
        tableService = new TableService(tableGroupingRepository, tableInfoRepositoryMock, transactionTemplate);
    }

    @Test
    void tableGroupingsWontHaveAnyInputsWhenTableInsertionsFails() {
        doThrow(RuntimeException.class).when(tableInfoRepositoryMock).insertTable(any());

        try {
            tableService.addTable("a-group", "an-alias", true);
        } catch (RuntimeException e) {
            //do nothing
        }

        assertThat(tableGroupingRepository.getAllTableGroups()).asList().isEmpty();
    }

}
