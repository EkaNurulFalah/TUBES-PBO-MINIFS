import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Directory extends Node {

    private List<Node> children = new ArrayList<>();

    public Directory(int id, String name, Directory parent, int ownerId) {
        super(id, name, parent, ownerId);
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getChild(String name) {
        for (Node n : children) {
            if (n.getName().equals(name)) {
                return n;
            }
        }
        return null;
    }

    public void listChildren() {
        for (Node n : children) {
            System.out.println(n.getName());
        }
    }

    public void deleteChild(String name) {
        Iterator<Node> it = children.iterator();
        while (it.hasNext()) {
            Node n = it.next();
            if (n.getName().equals(name)) {
                it.remove();
                return;
            }
        }
    }

    public void removeChild(Node target) {
        children.remove(target);
    }

    public boolean hasChild(String name) {
        return getChild(name) != null;
    }
}
