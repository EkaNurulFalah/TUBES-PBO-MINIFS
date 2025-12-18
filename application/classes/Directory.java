
import java.util.ArrayList;
import java.util.List;

public class Directory extends Node {
    private List<Node> children;

    public Directory(int id, String name, String type, Directory parent, List<Node> children) {
        super(id, name, type, parent);
        this.children = children;
    }
    public void addChild(Node node) {

    }
    public void removeChild(String name) {

    }
    public List<Node> listChildren() {

    }
     public String getInfo() {

    }
}