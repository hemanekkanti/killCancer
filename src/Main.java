public class Main {

    public static void main(String[] args) {

        Simulation s = new Simulation(
                Environment.plasma()
                        .withInitialCells(25)
                        .build()
        );
    }
}
