package inventariocliente.util;

import productModel.Product;
import inventariocliente.UID.*;
import java.io.*;
import java.net.Socket;
import javax.swing.JOptionPane;
import java.util.List;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Properties;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import request.Request;
import request.Response;

/**
 * Clase principal del cliente de inventario.
 */
public class InventarioCliente {

    private SSLSocket clientSocket;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;

    /**
     * Constructor que inicializa la conexión con el servidor.
     *
     * @param host Dirección del servidor.
     * @param port Puerto del servidor.
     * @throws IOException Si ocurre un error al conectar.
     */
    public InventarioCliente(String host, int port) throws IOException {

        // socket = new Socket(host, port);
        // output = new ObjectOutputStream(socket.getOutputStream());
        // input = new ObjectInputStream(socket.getInputStream())
        Properties p = new Properties();
        try {
            p.load(new FileInputStream(new File("properties.properties")));
        } catch (Exception ex) {
            System.out.println("error opteniendo las propiedades " + ex.getMessage());
        }
        String sslRoute = p.getProperty("SSL_CERTIFICATE_ROUTE");
        String sslPassword = p.getProperty("SSL_PASWORD");
        System.setProperty("javax.net.ssl.keyStore", sslRoute);
        System.setProperty("javax.net.ssl.keyStorePassword", sslPassword);
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.trustStore", sslRoute);
        System.setProperty("javax.net.ssl.trustStorePassword", sslPassword);
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
        SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        clientSocket = (SSLSocket) socketFactory.createSocket(host, port);
        System.out.println("Connection established");
        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        inputStream = new ObjectInputStream(clientSocket.getInputStream());
    }

    /**
     * Envía datos al servidor.
     *
     * @param request Datos a enviar.
     * @throws IOException Si ocurre un error al enviar.
     */
    public synchronized void enviarDatosAlServidor(Request request) throws IOException {
        outputStream.writeObject(request);
        outputStream.flush();
    }

    /**
     * Recibe datos del servidor.
     *
     * @return Datos recibidos.
     * @throws IOException Si ocurre un error al recibir.
     * @throws ClassNotFoundException Si la clase de los datos no se encuentra.
     */
    public synchronized Response recibirDatosDelServidor() throws IOException, ClassNotFoundException {
        return (Response) inputStream.readObject();
    }

    /**
     * Cierra la conexión con el servidor.
     *
     * @throws IOException Si ocurre un error al cerrar.
     */
    public void cerrarConexion() throws IOException {
        if (inputStream != null) {
            inputStream.close();
        }
        if (outputStream != null) {
            outputStream.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }

    /**
     * Agrega un producto al inventario.
     *
     * @param producto Producto a agregar.
     */
    public void agregarProducto(Product producto) {
        try {
            Request request = new Request("add", producto, "");
            enviarDatosAlServidor(request);
            Response respuesta = recibirDatosDelServidor();
            JOptionPane.showMessageDialog(null, respuesta.getMenssage(), "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al agregar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Elimina un producto del inventario.
     *
     * @param codigo Código del producto a eliminar.
     */
    public void eliminarProducto(String codigo) {
        try {
            Request request = new Request("remove", codigo);
            enviarDatosAlServidor(request);
            Response respuesta = recibirDatosDelServidor();
            JOptionPane.showMessageDialog(null, respuesta.getMenssage(), "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Actualiza un producto en el inventario.
     *
     * @param producto Producto a actualizar.
     */
    public void actualizarProducto(Product producto) {
        try {
            Request request = new Request("update", producto, "");
            enviarDatosAlServidor(request);
            Response respuesta = recibirDatosDelServidor();
            JOptionPane.showMessageDialog(null, respuesta.getMenssage(), "Información", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Busca un producto en el inventario.
     *
     * @param nombre Nombre del producto a buscar.
     * @return Producto encontrado o null si no se encuentra.
     */
    public Product buscarProducto(String nombre) {
        try {
            Request request = new Request("find", nombre);
            enviarDatosAlServidor(request);
            Response respuesta = recibirDatosDelServidor();
            return respuesta.getProduct();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar producto: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Exporta el inventario completo.
     *
     * @return Lista de productos en el inventario.
     */
    public ArrayList<Product> exportarInventario() {
        try {
            Request request = new Request("view", "");
            enviarDatosAlServidor(request);
            Response respuesta = recibirDatosDelServidor();
            return respuesta.getListProduct();
        } catch (IOException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error al exportar inventario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    /**
     * Exporta el inventario a un archivo CSV.
     *
     * @param inventario Lista de productos en el inventario.
     * @param filePath Ruta del archivo CSV.
     */
    public void exportarInventarioACSV(List<Product> inventario, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.append("Código,Nombre,Cantidad,Precio,Descripción\n");

            for (Product producto : inventario) {
                writer.append(Long.toString(producto.getId()))
                        .append(",")
                        .append(producto.getName())
                        .append(",")
                        .append(String.valueOf(producto.getAvailable()))
                        .append(",")
                        .append(String.valueOf(producto.getPrice()))
                        .append(",")
                        .append(producto.getDescription())
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

                // String host = args[0];
                // int port = Integer.parseInt(args[1]);
                InventarioCliente gestor = new InventarioCliente("25.64.101.103", 9090);
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
