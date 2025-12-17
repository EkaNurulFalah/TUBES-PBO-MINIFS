package application.classes;

public class Node {

    private int id;
    private String name;
    private String type;
    private Directory parent;
    private User owner;
    private String createdAt;

    public Node(
        int id,
        String name,
        String type,
        Directory parent,
        User owner,
        String createdAt
    ) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parent = parent;
        this.owner = owner;
        this.createdAt = createdAt;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return "";
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public String getType() {
        return type;
    }
}
