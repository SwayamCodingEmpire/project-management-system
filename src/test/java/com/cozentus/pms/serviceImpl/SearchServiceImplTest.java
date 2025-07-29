
package com.cozentus.pms.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SearchServiceImplTest {

    @MockBean
    private VectorStore vectorStore;

    private SearchServiceImpl searchService;

    @BeforeEach
    public void setUp() {
        this.searchService = new SearchServiceImpl(vectorStore);
    }

    @Test
    public void testSearchWithValidQuery() {
        // Arrange
        String query = "valid query";
        Mockito.when(vectorStore.similaritySearch(query)).thenReturn(Collections.emptyList());

        // Act
        searchService.search(query);

        // Assert
        verify(vectorStore, times(1)).similaritySearch(query);
    }

    @Test
    public void testSearchWithEmptyQuery() {
        // Arrange
        String query = "";

        // Act
        searchService.search(query);

        // Assert
        verify(vectorStore, times(1)).similaritySearch(query);
    }

    @Test
    public void testSearchWithNullQuery() {
        // Arrange
        String query = null;

        // Act
        searchService.search(query);

        // Assert
        verify(vectorStore, times(1)).similaritySearch(query);
    }

    @Test
    public void testSearchWithMultipleQueries() {
        // Arrange
        String query1 = "query1";
        String query2 = "query2";
        Mockito.when(vectorStore.similaritySearch(anyString())).thenReturn(Collections.emptyList());

        // Act
        searchService.search(query1);
        searchService.search(query2);

        // Assert
        verify(vectorStore, times(1)).similaritySearch(query1);
        verify(vectorStore, times(1)).similaritySearch(query2);
    }
}
