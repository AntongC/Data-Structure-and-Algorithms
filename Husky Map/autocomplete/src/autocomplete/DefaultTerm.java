package autocomplete;

/**
 * This is currently the only implementation of the {@link Term} interface, which is why it's named
 * "default." (Having an interface with a single implementation is a little redundant, but we need
 * it to keep you from accidentally renaming things.)
 * <p>
 * Make sure to check out the interface for method specifications.
 *
 * @see Term
 */
public class DefaultTerm implements Term {
    String query;
    long weight;

    /**
     * Initializes a term with the given query string and weight.
     *
     * @throws IllegalArgumentException if query is null or weight is negative
     */
    public DefaultTerm(String query, long weight) {
        if (query == null) {
            throw new IllegalArgumentException("Can't have null query");
        }
        if (weight < 0) {
            throw new IllegalArgumentException("Can't have negative weight");
        }
        this.query = query;
        this.weight = weight;
    }

    @Override
    public String query() {
        return query;
    }

    @Override
    public long weight() {
        return weight;
    }

    @Override
    public int queryOrder(Term that) {
        return query.compareTo(that.query());
    }

    @Override
    public int reverseWeightOrder(Term that) {
        return Long.compare(that.weight(), weight);
    }

    @Override
    public int matchesPrefix(String prefix) {
        if (prefix.length() > query.length()) {
            return -1;
        }
        return query.substring(0, prefix.length()).compareTo(prefix);
    }

    @Override
    public String toString() {
        return "DefaultTerm{" +
            "query='" + query + '\'' +
            ", weight=" + weight +
            '}';
    }
}
