public class User {
    protected String name, height, weight, email, dob;

    public User(){
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(String name, String height, String weight, String email, String dob) {
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.email = email;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
