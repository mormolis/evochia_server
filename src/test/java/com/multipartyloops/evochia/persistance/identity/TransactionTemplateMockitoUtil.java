package com.multipartyloops.evochia.persistance.identity;

import org.mockito.stubbing.Answer;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;

public class TransactionTemplateMockitoUtil {
    public static <T> Answer<T> callConsumer() {
        return invocation -> {
            final Consumer<TransactionStatus> firstArgumentAsConsumer = invocation.getArgument(0);
            firstArgumentAsConsumer.accept(mock(TransactionStatus.class));
            return null;
        };
    }

    public static Answer<Object> callTransactionCallback() {
        return invocation -> {
            final TransactionCallback<Object> firstArgumentAsTransactionCallback = invocation.getArgument(0);
            return firstArgumentAsTransactionCallback.doInTransaction(mock(TransactionStatus.class));
        };
    }
}
