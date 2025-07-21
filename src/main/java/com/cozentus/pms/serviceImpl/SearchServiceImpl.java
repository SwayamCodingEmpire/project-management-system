package com.cozentus.pms.serviceImpl;

import org.springframework.ai.vectorstore.VectorStore;

public class SearchServiceImpl {
	private final VectorStore vectorStore;
	
	public SearchServiceImpl(VectorStore vectorStore) {
		this.vectorStore = vectorStore;
	}
	
	public void search(String query) {

		
		// Perform the search using the vector store
		var searchResults = vectorStore.similaritySearch(query);
		
	}
	

}
