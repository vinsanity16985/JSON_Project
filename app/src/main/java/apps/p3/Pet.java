package apps.p3;

public class Pet {
    //Name of the Pet
    private String name;
    //File name of the Pet image
    private String picture;

    public Pet(){
        this.name = null;
        this.picture = null;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setPicture(String picture){
        this.picture = picture;
    }
    public String getPicture(){
        return this.picture;
    }
}
