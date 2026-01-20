package negocio;

public class ProductoRegular extends Producto  {
    
    public ProductoRegular(String codigo, String modelo, String talla, double precio, int stock, int stockMin) {
        super(codigo, modelo, talla, precio, stock, stockMin);
    }

    @Override
    public String getTipo() {
        return "Regular";
    }
}
