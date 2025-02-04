package productModel;

import java.io.Serializable;


public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    public long id;
    public String name;
    public String description;
    public double price;
    public int available;

    public Product(long id, String name, String description, double price, int available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.available = available;
    }

    public long getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String desription) {
        this.description = desription;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAvailable() {
        return this.available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }
    
    @Override
    public String toString() {
        return "Producto{" +
                "nombre='" + name + '\'' +
                ", codigo='" + id + '\'' +
                ", cantidad=" + available +
                ", precio=" + price +
                ", descripcion='" + description + '\'' +
                '}';
    }

}