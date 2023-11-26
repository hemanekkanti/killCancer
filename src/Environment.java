public final class Environment {

    /**
     * The number of healthy cells at the start of the simulation.
     */
    private final int initialCells;

    public int getInitialCells() {
        return initialCells;
    }

    private Environment(final int initialCells) {
        this.initialCells = initialCells;
    }

    public static Builder plasma() {
        return new Builder();
    }

    public static final class Builder {

        private int initialCells = 25;

        public Builder withInitialCells(final int initialCells) {
            this.initialCells = initialCells;
            return this;
        }

        public Environment build() {
            return new Environment(initialCells);
        }

    }

}
