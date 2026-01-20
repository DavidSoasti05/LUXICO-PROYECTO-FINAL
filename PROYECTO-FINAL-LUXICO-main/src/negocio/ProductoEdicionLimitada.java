package negocio;

public class ProductoEdicionLimitada extends Producto {

    private int numeroSerie;

    public ProductoEdicionLimitada(String codigo, String modelo, String talla, double precio, int stock, int stockMin, int numeroSerie) {
        super(codigo, modelo, talla, precio, stock, stockMin);
        this.numeroSerie = (numeroSerie > 0) ? numeroSerie : 1;
    }

    public ProductoEdicionLimitada(String codigo, String modelo, String talla, double precio, int stock, int stockMin) {
        this(codigo, modelo, talla, precio, stock, stockMin, 1);
    }

    @Override
    public String getTipo() {
        return "Edición Limitada";
    }

    public int getNumeroSerie() { return numeroSerie; }
}
