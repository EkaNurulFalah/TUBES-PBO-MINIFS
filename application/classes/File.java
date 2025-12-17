package application.classes;

public class File extends Node {

    private String content;

    public File(int id, String name, Directory parent, User owner) {
        super(id, name, "file", parent, owner, "01-01-1999");
        this.content = "";
    }

    public String read() {
        return "";
    }

    public void write(String text) {}

    public String getInfo() {
        return "";
    }
}
