public abstract class Node {

    protected int id;
    protected String name;
    protected Directory parent;
    protected int ownerId;

    protected Node(int id, String name, Directory parent, int ownerId) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public String getPath() {
        if (parent == null) return "~";
        return parent.getPath() + "/" + name;
    }

    public int getId() {
        return id;
    }

    public abstract boolean isDirectory();
}
