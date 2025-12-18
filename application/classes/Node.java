
public abstract class Node {

    protected int id;
    protected String name;
    protected User owner;
    protected Directory parent;

    public Node(int id, String name, User owner, Directory parent) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public User getOwner(){
        return owner;
    }

    public abstract String getInfo();
}



// public abstract class Node {

//     private int id;
//     private String name;
//     private String type;
//     private Directory parent;
//     private User owner;
//     // private String createdAt;

//     public Node(int id, String name, String , Directory parent) {
//         this.id = id;
//         this.name = name;
//         // this.type = type;
//         this.parent = parent;
//         this.owner = owner;
//         // this.createdAt = createdAt;
//     }

//     public String getName() {

//     }

//     public String setName(String name) {

//     }

//     public Directory getParent() {
//         return parent;
//     }

//     public void setParent(Directory parent) {
//         this.parent = parent;
//     }

//     public String getPath() {

//     }

// }
