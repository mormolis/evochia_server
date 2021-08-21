package com.multipartyloops.evochia.core.table;

import com.multipartyloops.evochia.core.commons.exceptions.ValueCannotBeNullOrEmptyException;
import com.multipartyloops.evochia.core.table.dto.TableGroupingDto;
import com.multipartyloops.evochia.core.table.dto.TableInfoDto;
import com.multipartyloops.evochia.persistance.table.TableInfoRepository;
import com.multipartyloops.evochia.persistance.table.grouping.TableGroupingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.multipartyloops.evochia.persistance.identity.TransactionTemplateMockitoUtil.callConsumer;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class TableServiceTest {

    @Mock
    private TableGroupingRepository<TableGroupingDto> tableGroupingRepositoryMock;
    @Mock
    private TableInfoRepository<TableInfoDto> tableInfoRepositoryMock;
    @Mock
    private TransactionTemplate transactionTemplateMock;

    @Captor
    private ArgumentCaptor<TableInfoDto> tableInfoDtoArgumentCaptor;
    @Captor
    private ArgumentCaptor<TableGroupingDto> tableGroupingDtoArgumentCaptor;

    private TableService tableService;

    @BeforeEach
    void setUp() {
        tableService = new TableService(tableGroupingRepositoryMock, tableInfoRepositoryMock, transactionTemplateMock);
    }

    @Test
    void addsANewTableOnExistingGroup() {
        doAnswer(callConsumer()).when(transactionTemplateMock).executeWithoutResult(any());
        final var groupName = new TableGroupingDto(UUID.randomUUID().toString(), "groupName", true);
        given(tableGroupingRepositoryMock.getTableGroupByName("groupName")).willReturn(Optional.of(groupName));

        tableService.addTable("groupName", "alias", true);

        then(tableInfoRepositoryMock).should().insertTable(tableInfoDtoArgumentCaptor.capture());
        assertThat(tableInfoDtoArgumentCaptor.getValue().getTableAlias()).isEqualTo("alias");
        assertThat(tableInfoDtoArgumentCaptor.getValue().getGroupId()).isEqualTo(groupName.getGroupId());
        assertThat(tableInfoDtoArgumentCaptor.getValue().getEnabled()).isTrue();
    }

    @Test
    void addsANewTableOnNonExistingGroup() {
        doAnswer(callConsumer()).when(transactionTemplateMock).executeWithoutResult(any());
        given(tableGroupingRepositoryMock.getTableGroupByName("groupName")).willReturn(Optional.empty());

        tableService.addTable("groupName", "alias", true);

        then(tableGroupingRepositoryMock).should().insertTableGroup(tableGroupingDtoArgumentCaptor.capture());
        assertThat(tableGroupingDtoArgumentCaptor.getValue().getGroupName()).isEqualTo("groupName");
        assertThat(tableGroupingDtoArgumentCaptor.getValue().getEnabled()).isTrue();
        then(tableInfoRepositoryMock).should().insertTable(tableInfoDtoArgumentCaptor.capture());
        assertThat(tableInfoDtoArgumentCaptor.getValue().getTableAlias()).isEqualTo("alias");
        assertThat(tableInfoDtoArgumentCaptor.getValue().getGroupId()).isEqualTo(tableGroupingDtoArgumentCaptor.getValue().getGroupId());
        assertThat(tableInfoDtoArgumentCaptor.getValue().getEnabled()).isTrue();
    }

    @Test
    void addTableThrowsWhenTableGroupNameIsNullOrEmpty() {
        assertThatThrownBy(() -> tableService.addTable(null, "alias", true))
                .isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.addTable("", "alias", true))
                .isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }

    @Test
    void addTableAliasThrowsWhenTableGroupNameIsEmpty() {
        assertThatThrownBy(() -> tableService.addTable("group", null, true))
                .isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.addTable("group", "", true))
                .isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }

    @Test
    void deletesTable() {
        given(tableInfoRepositoryMock.getTableByAlias("alias")).willReturn(Optional.of(new TableInfoDto("id", "alias", "groupId", true)));

        tableService.removeTable("alias");

        then(tableInfoRepositoryMock).should().deleteTable("id");
    }

    @Test
    void deletesTableShouldThrowWhenAliasPassedIsNullOrEmpty() {
        assertThatThrownBy(() -> tableService.removeTable(null)).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.removeTable("")).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }

    @Test
    void disablesATable() {
        given(tableInfoRepositoryMock.getTableByAlias("alias")).willReturn(Optional.of(new TableInfoDto("id", "alias", "groupId", true)));

        tableService.disableTable("alias");

        then(tableInfoRepositoryMock).should().disableTable("id");
    }

    @Test
    void disablesTableThrowsWhenTableAliasIsNullOrEmpty() {
        assertThatThrownBy(() -> tableService.disableTable(null)).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.disableTable("")).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }

    @Test
    void enablesATable() {
        given(tableInfoRepositoryMock.getTableByAlias("alias")).willReturn(Optional.of(new TableInfoDto("id", "alias", "groupId", false)));

        tableService.enableTable("alias");

        then(tableInfoRepositoryMock).should().enableTable("id");
    }

    @Test
    void enablesTableThrowsWhenTableAliasIsNullOrEmpty() {
        assertThatThrownBy(() -> tableService.enableTable(null)).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.enableTable("")).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }

    @Test
    void enablesTableGroup() {
        given(tableInfoRepositoryMock.getAllTables()).willReturn(List.of(
                new TableInfoDto("id1", "alias1", "groupId", false),
                new TableInfoDto("id2", "alias2", "groupId1", false),
                new TableInfoDto("id3", "alias3", "groupId", false)
        ));

        tableService.enableTableGroup("groupId1");

        then(tableInfoRepositoryMock).should().getAllTables();
        then(tableInfoRepositoryMock).should().enableTable("id2");
        verifyNoMoreInteractions(tableInfoRepositoryMock);
    }

    @Test
    void enablesTableGroupThrowsWhenTableAliasIsNullOrEmpty() {
        assertThatThrownBy(() -> tableService.enableTableGroup(null)).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.enableTableGroup("")).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }

    @Test
    void disablesTableGroup() {
        given(tableInfoRepositoryMock.getAllTables()).willReturn(List.of(
                new TableInfoDto("id1", "alias1", "groupId", true),
                new TableInfoDto("id2", "alias2", "groupId1", true),
                new TableInfoDto("id3", "alias3", "groupId", true)
        ));

        tableService.disableTableGroup("groupId1");

        then(tableInfoRepositoryMock).should().getAllTables();
        then(tableInfoRepositoryMock).should().disableTable("id2");
        verifyNoMoreInteractions(tableInfoRepositoryMock);
    }

    @Test
    void disablesTableGroupThrowsWhenTableAliasIsNullOrEmpty() {
        assertThatThrownBy(() -> tableService.disableTableGroup(null)).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
        assertThatThrownBy(() -> tableService.disableTableGroup("")).isInstanceOf(ValueCannotBeNullOrEmptyException.class);
    }
}