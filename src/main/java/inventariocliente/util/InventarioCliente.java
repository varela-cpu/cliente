package inventariocliente.util;
import inventariocliente.UID.*;
import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Clase principal del cliente de inventario.
 */
public class InventarioCliente {
    private Socket socket;
    private ObjectInputStream input;
    private ObjectOutputStream output;

    /**
     * Constructor que inicializa la conexión con el servidor.
     * @param host Dirección del servidor.
     * @param port Puerto del servidor.
     * @throws IOException Si ocurre un error al conectar.
     */
    public InventarioCliente(String host, int port) throws IOException {
        
         socket = new Socket(host, port);
         output = new ObjectOutputStream(socket.getOutputStream());
         input = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Envía datos al servidor.
     * @param data Datos a enviar.
     * @throws IOException Si ocurre un error al enviar.
     */
    public void enviarDatosAlServidor(Object data) throws IOException {
         output.writeObject(data);
         output.flush();
    }

    /**
     * Recibe datos del servidor.
     * @return Datos recibidos.
     * @throws IOException Si ocurre un error al recibir.
     * @throws ClassNotFoundException Si la clase de los datos no se encuentra.
     */
    public Object recibirDatosDelServidor() throws IOException, ClassNotFoundException {
        return input.readObject();
    }

    /**
     * Cierra la conexión con el servidor.
     * @throws IOException Si ocurre un error al cerrar.
     */
    public void cerrarConexion() throws IOException {
         input.close();
         output.close();
         socket.close();
    }

    /**
     * Agrega un producto al inventario.
     * @param producto Producto a agregar.
     */
    public void agregarProducto(Producto producto) {
        try {
             enviarDatosAlServidor("AGREGAR");
             enviarDatosAlServidor(producto);
             String respuesta = (String) recibirDatosDelServidor();
             JOptionPane.showMessageDialog(null, respuesta, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un producto del inventario.
     * @param codigo Código del producto a eliminar.
     */
    public void eliminarProducto(String codigo) {
        try {
             enviarDatosAlServidor("ELIMINAR");
             enviarDatosAlServidor(codigo);
             String respuesta = (String) recibirDatosDelServidor();
             JOptionPane.showMessageDialog(null, respuesta, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza un producto en el inventario.
     * @param producto Producto a actualizar.
     */
    public void actualizarProducto(Producto producto) {
        try {
             enviarDatosAlServidor("ACTUALIZAR");
             enviarDatosAlServidor(producto);
             String respuesta = (String) recibirDatosDelServidor();
             JOptionPane.showMessageDialog(null, respuesta, "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca un producto en el inventario.
     * @param nombre Nombre del producto a buscar.
     * @return Producto encontrado o null si no se encuentra.
     */
    public Producto buscarProducto(String nombre) {
        try {
             enviarDatosAlServidor("BUSCAR");
             enviarDatosAlServidor(nombre);
             return (Producto) recibirDatosDelServidor();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Exporta el inventario completo.
     * @return Lista de productos en el inventario.
     */
    public List<Producto> exportarInventario() {
        try {
             enviarDatosAlServidor("EXPORTAR");
             return (List<Producto>) recibirDatosDelServidor();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al exportar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    /**
     * Exporta el inventario a un archivo CSV.
     * @param inventario Lista de productos en el inventario.
     * @param filePath Ruta del archivo CSV.
     */
    public void exportarInventarioACSV(List<Producto> inventario, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
           
            writer.append("Código,Nombre,Cantidad,Precio,Descripción\n");

            
            for (Producto producto : inventario) {
                writer.append(producto.getCodigo())
                      .append(",")
                      .append(producto.getNombre())
                      .append(",")
                      .append(String.valueOf(producto.getCantidad()))
                      .append(",")
                      .append(String.valueOf(producto.getPrecio()))
                      .append(",")
                      .append(producto.getDescripcion())
                      .append("\n");
            }

            JOptionPane.showMessageDialog(null, "Inventario exportado a CSV con éxito.", "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al exportar inventario a CSV: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                 
                 String host = args[0];
                 int port = Integer.parseInt(args[1]);
                 InventarioCliente gestor = new InventarioCliente(host, port);
                VentanaPrincipal ventana = new VentanaPrincipal();
                 ventana.setInventarioCliente(gestor);
                ventana.setVisible(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al conectar con el servidor: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El puerto debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}