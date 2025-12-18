
import java.util.ArrayList;
import java.util.List;

public class Directory extends Node {
    private List<Node> children;

    public Directory(int id, String name, User owner, Directory parent) {
        super(id, name, owner, parent);
        this.children = new ArrayList<>();
    }
    public void addChild(Node node) {
        children.add(node);
        node.setParent(this);
    }
    public void removeChild(String name) {
        children.removeIf(child -> child.getName().equals(name));
    }
    public List<Node> listChildren() {
        return children;
    }

    // @Override
     public String getInfo() {
        return "[DIR]" + name;
    }
}