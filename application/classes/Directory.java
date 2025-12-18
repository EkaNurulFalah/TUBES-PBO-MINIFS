package application.classes;

import java.util.ArrayList;

public class Directory extends Node {

    private ArrayList<Node> children;
    private String path;

    public Directory() {
        super(111, "/", "directory", null, null, "01-01-2001");
        path = "~";
        children = new ArrayList<>();
        children.add(new File(222, "file.txt", this, null));
        children.add(new File(333, "hello.txt", this, null));
        children.add(new Directory("folder", this));
    }

    public Directory(String name, Directory parent) {
        super(777, name, "directory", parent, null, "01-01-2001");
        path = parent.getPath() + "/" + name;
    }

    public void listChildren() {
        if (children == null) return;

        for (Node node : children) {
            System.out.println(node.getName());
        }
    }

    public String getPath() {
        return path;
    }

    public Node getChild(String target) {
        for (Node node : children) {
            if (
                node.getType().equals("directory") &&
                node.getName().equals(target)
            ) {
                return node;
            }
        }

        return null;
    }

    public void addChild(Directory directory) {
        children.add(directory);
    }
}
