package ccetl.customcapes;

import java.util.ArrayList;
import java.util.List;

public class util {

    public static final util INSTANCE = new util();

    public final List<String> namesOfPlayersWithSavedCape = new ArrayList<>();
    public final List<String> namesOfPlayersWhoDoNotHaveACape = new ArrayList<>();
    public final List<String> namesOfPlayersWhoDoHaveACape = new ArrayList<>();

    public void addToNamesOfPlayersWithSavedCape(String nameToAdd) {
        namesOfPlayersWithSavedCape.add(nameToAdd);
    }

    public void addToNamesOfPlayersWhoDoNotHaveACape(String nameToAdd) {
        namesOfPlayersWhoDoNotHaveACape.add(nameToAdd);
    }

    public void addToNamesOfPlayersWhoDoHaveACape(String nameToAdd) {
        namesOfPlayersWhoDoHaveACape.add(nameToAdd);
    }

    public List<String> getNamesOfPlayersWhoDoHaveACape() {
        return namesOfPlayersWhoDoHaveACape;
    }

    public List<String> getNamesOfPlayersWhoDoNotHaveACape() {
        return namesOfPlayersWhoDoNotHaveACape;
    }

    public List<String> getNamesOfPlayersWithSavedCape() {
        return namesOfPlayersWithSavedCape;
    }
}
