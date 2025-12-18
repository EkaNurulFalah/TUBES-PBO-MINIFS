public class File extends Node implements Readable, Writeable {

    private String content;

    public File(
        int id,
        String name,
        Directory parent,
        int ownerId,
        String content
    ) {
        super(id, name, parent, ownerId);
        this.content = content;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public void read() {
        System.out.println(content);
    }

    @Override
    public void write(String text) {
        content = text;
    }
}
