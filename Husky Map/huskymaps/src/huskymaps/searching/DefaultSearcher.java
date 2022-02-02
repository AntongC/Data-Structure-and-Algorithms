package huskymaps.searching;

import autocomplete.Autocomplete;
import autocomplete.DefaultTerm;
import autocomplete.Term;
import huskymaps.graph.Node;
import huskymaps.graph.StreetMapGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @see Searcher
 */
public class DefaultSearcher extends Searcher {
    HashMap<String, List<Node>> exactName;
    List<Term> nodeArray;
    Autocomplete binarySearcher;

    public DefaultSearcher(StreetMapGraph graph) {
        nodeArray = new ArrayList<>(1);
        exactName = new HashMap<>();
        for (Node element : graph.allNodes()) {
            if (!exactName.containsKey(element.name())) {
                List<Node> temp = new ArrayList<>(1);
                temp.add(element);
                exactName.put(element.name(), temp);
            } else {
                List<Node> temp = exactName.get(element.name());
                temp.add(element);
                exactName.replace(element.name(), temp);
            }
            if (element.name() != null) {
                nodeArray.add(createTerm(element.name(), element.importance()));
            }
        }
        binarySearcher = createAutocomplete(nodeArray.toArray(new Term[0]));
    }

    @Override
    protected Term createTerm(String name, int weight) {
        return new DefaultTerm(name, weight);
    }

    @Override
    protected Autocomplete createAutocomplete(Term[] termsArray) {
        return new Autocomplete(termsArray);
    }

    @Override
    public List<String> getLocationsByPrefix(String prefix) {
        Term[] result = binarySearcher.findMatchesForPrefix(prefix);
        List<String> retVal = new ArrayList<>(1);
        for (Term element : result) {
            if (!retVal.contains(element.query())) {
                retVal.add(element.query());
            }
        }
        return retVal;
    }

    @Override
    public List<Node> getLocations(String locationName) {
        return exactName.get(locationName);
    }
}
