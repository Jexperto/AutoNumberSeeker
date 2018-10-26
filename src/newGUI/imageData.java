package newGUI;

public class imageData {
    private String path;
    private byte state;
    private int x;
    private int y;
    private String number;

    public imageData(String path) {
        this.path = path;
        this.state = 0;
    }

    public String getPath() {
        return path;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
