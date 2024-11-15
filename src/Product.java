import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;

public class Product implements Serializable {
    private static final int NAME_LENGTH = 35;
    private static final int DESC_LENGTH = 75;
    private static final int ID_LENGTH = 6;

    private String ID;
    private String name;
    private String desc;
    private double cost;

    public Product(String ID, String name, String desc, double cost) {
        this.ID = formatID(ID);
        this.name = formatName(name);
        this.desc = formatDesc(desc);
        this.cost = cost;
    }

    private String formatID(String id) {
        return String.format("%-" + ID_LENGTH + "s", id).substring(0, ID_LENGTH);
    }

    private String formatName(String name) {
        return String.format("%-" + NAME_LENGTH + "s", name).substring(0, NAME_LENGTH);
    }

    private String formatDesc(String desc) {
        return String.format("%-" + DESC_LENGTH + "s", desc).substring(0, DESC_LENGTH);
    }



    @Override
    public String toString() {
        return "Product{" +
                "ID='" + ID.trim() + '\'' +
                ", name='" + name.trim() + '\'' +
                ", desc='" + desc.trim() + '\'' +
                ", cost=" + cost +
                '}';
    }

    public void writeToFile(RandomAccessFile raf) throws IOException {
        raf.writeBytes(formatID(ID) + "      ");
        raf.writeBytes(formatName(name));
        raf.writeBytes(formatDesc(desc));
        raf.writeBytes(String.valueOf(cost));
        raf.writeBytes("\n");
    }

    public String getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public double getCost() {
        return cost;
    }

}
