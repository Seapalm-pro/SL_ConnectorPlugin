package fr.mrbaguette07.slconnector;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface BridgedSuggestions<S> {

    public default List<String> suggest(S sender, String label, String[] args) {
        return Collections.emptyList();
    }

    public default CompletableFuture<List<String>> suggestAsync(S sender, String label, String[] args) {
        return CompletableFuture.completedFuture(suggest(sender, label, args));
    }
}
