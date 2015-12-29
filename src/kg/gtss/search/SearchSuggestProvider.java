package kg.gtss.search;

import android.content.SearchRecentSuggestionsProvider;

public class SearchSuggestProvider extends SearchRecentSuggestionsProvider {
	final static String AUTHORITY = "kg.gtss.searchSuggestProvider";
	final static int MODE = DATABASE_MODE_QUERIES;

	public SearchSuggestProvider() {
		super();
		this.setupSuggestions(AUTHORITY, MODE);
	}
}
