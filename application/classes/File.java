public class File extends Node {
    private String content;


    public File(int id, String name, User owner, Directory parent, String content) {
        super(id, name, owner, parent);
        this.content = content;
    }
    public String read() {
        return content;
    }
    public void write(String text) {
        this.content = text;
    }

    // @Override
     public String getInfo() {
        return "[FILE]" + name;
    }
}
