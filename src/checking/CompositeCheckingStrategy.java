package src.checking;

import java.util.ArrayList;
import java.util.List;

public class CompositeCheckingStrategy implements ICheckingStrategy{


     private ArrayList<ICheckingStrategy> checkingStrategies;
    public CompositeCheckingStrategy(ArrayList<ICheckingStrategy> checkingStrategies) {
        this.checkingStrategies = checkingStrategies;

    }

    @Override
    public boolean check() {
        for (ICheckingStrategy strategy: checkingStrategies) {
            boolean valid = strategy.check();
            if (!valid) {
                return false;
            }
        }
    return true;
    }

    public ArrayList<ICheckingStrategy> getCheckingStrategies() {
        return this.checkingStrategies;
    }
}
