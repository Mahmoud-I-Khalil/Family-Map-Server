package Utilities;

import java.util.List;

public class Locations {

    List<Location> data;

    public Locations(List<Location> data) {
        this.data = data;
    }

    public List<Location> getData() {
        return data;
    }

    public void setData(List<Location> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < data.size(); i++){
            stringBuilder.append(data.get(i).toString());
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
