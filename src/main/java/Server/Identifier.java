package Server;

import java.util.ArrayList;
import java.util.List;

public class Identifier {

    private static List<Integer> ids = new ArrayList<Integer>();
    private static final int range = 8;
    private static int index = 0;

    static {
        for (int i = 0; i < range; i++) {
            ids.add(i);
        }
    }

    private Identifier() {
    }

    public static int getIdentifier(){
        if(index > ids.size() - 1){index = 0;}
        return ids.get(index++);
    }
}
