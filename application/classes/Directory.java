package application.classes;

import java.util.ArrayList;
import java.util.Iterator;

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
        children = new ArrayList<>();
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
            if (node.getName().equals(target)) {
                return node;
            }
        }

        return null;
    }

    public void addChild(Node child) {
        children.add(child);
    }

    public void deleteChild(String name) {
        Iterator<Node> iterator = children.iterator();

        while (iterator.hasNext()) {
            Node child = iterator.next();

            if (child.getName().equals(name)) {
                // If directory, delete all its contents recursively
                if (child instanceof Directory) {
                    Directory dir = (Directory) child;
                    dir.deleteAllChildren();
                }

                // Remove the node itself (file or directory)
                iterator.remove();
                return;
            }
        }
    }

    private void deleteAllChildren() {
        for (Node child : children) {
            if (child instanceof Directory) {
                Directory dir = (Directory) child;
                dir.deleteAllChildren();
            }
        }
        children.clear();
    }
}
