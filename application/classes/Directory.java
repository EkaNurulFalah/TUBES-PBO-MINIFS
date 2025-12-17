package application.classes;

import java.util.ArrayList;

public class Directory {

    private ArrayList<Node> children;

    public Directory(ArrayList<Node> children) {
        this.children = children;
    }

    public void addChild(Node node) {}

    public void removeChild(String name) {}

    public ArrayList<Node> listChildren() {
        return null;
    }

    public String getInfo() {
        return "";
    }
}
