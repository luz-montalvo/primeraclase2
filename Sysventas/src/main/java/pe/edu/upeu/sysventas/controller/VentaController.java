
package pe.edu.upeu.sysventas.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import pe.edu.upeu.sysventas.components.*;
import pe.edu.upeu.sysventas.dto.ModeloDataAutocomplet;
import pe.edu.upeu.sysventas.dto.PersonaDto;
import pe.edu.upeu.sysventas.dto.SessionManager;
import pe.edu.upeu.sysventas.enums.TipoDocumento;
import pe.edu.upeu.sysventas.enums.TipoProducto;
import pe.edu.upeu.sysventas.exception.ModelNotFoundException;
import pe.edu.upeu.sysventas.model.Cliente;
import pe.edu.upeu.sysventas.model.VentCarrito;
import pe.edu.upeu.sysventas.model.Venta;
import pe.edu.upeu.sysventas.model.VentaDetalle;
import pe.edu.upeu.sysventas.service.*;
import pe.edu.upeu.sysventas.utils.ConsultaDNI;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

public class VentaController {

    @FXML
    TextField autocompProducto;

    @FXML
    TextField nombreProducto, codigoPro, stockPro, cantidadPro,
            punitPro, preTPro, txtBaseImp, txtIgv,
            txtDescuento, txtImporteT;

    @FXML
    Button btnRegVenta, btnRegCarrito, btnFormCliente, btnRegCliente;

    @FXML
    TextField autocompCliente, txtDireccion;

    @FXML
    TextField razonSocial;

    @FXML
    TextField dniRuc;

    @FXML
    TableView<VentCarrito> tableView;

    AutoCompleteTextField actf;
    ModeloDataAutocomplet lastProducto;

    AutoCompleteTextField actfC;
    ModeloDataAutocomplet lastCliente;

    IProductoService ps;
    IClienteService cs;
    IVentaCarritoService daoC;
    IUsuarioService daoU;
    IVentaService daoV;
    IVentaDetalleService daoVD;

    Stage stage;

    @FXML
    private AnchorPane miContenedor;

    private final SortedSet<ModeloDataAutocomplet> entries =
            new TreeSet<>((o1, o2) ->
                    o1.toString().compareTo(o2.toString()));

    private final SortedSet<ModeloDataAutocomplet> entriesC =
            new TreeSet<>((o1, o2) ->
                    o1.toString().compareTo(o2.toString()));

    ConsultaDNI cDni;
    TipoProducto tipoProducto;

    public VentaController(
            IProductoService ps,
            IClienteService cs,
            IVentaCarritoService daoC,
            IUsuarioService daoU,
            IVentaService daoV,
            IVentaDetalleService daoVD,
            ConsultaDNI cDni) {

        this.ps = ps;
        this.cs = cs;
        this.daoC = daoC;
        this.daoU = daoU;
        this.daoV = daoV;
        this.daoVD = daoVD;
        this.cDni = cDni;
    }

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            stage = (Stage) miContenedor.getScene().getWindow();
            System.out.println(
                    "El título del stage es: " + stage.getTitle());
        });

        listarCliente();
        autoCompletarCliente();

        listarProducto();

        actf = new AutoCompleteTextField<>(entries, autocompProducto);

        autocompProducto.setOnKeyReleased(e -> {
            lastProducto =
                    (ModeloDataAutocomplet) actf.getLastSelectedObject();

            if (lastProducto != null) {
                System.out.println(lastProducto.getNameDysplay());

                nombreProducto.setText(lastProducto.getNameDysplay());
                codigoPro.setText(lastProducto.getIdx());

                String[] dato = lastProducto.getOtherData().split(":");

                punitPro.setText(dato[0]);
                stockPro.setText(dato[1]);
                tipoProducto = TipoProducto.valueOf(dato[2]);
            }
        });

        personalizarTabla();

        btnRegCarrito.setDisable(true);
        btnRegCliente.setDisable(true);
    }

    public void listarProducto() {
        entries.clear();
        entries.addAll(ps.listAutoCompletProducto());
    }

    public void listarCliente() {
        entriesC.clear();
        entriesC.addAll(cs.listAutoCompletCliente());
    }

    public void autoCompletarCliente() {

        actfC = new AutoCompleteTextField<>(entriesC, autocompCliente);

        autocompCliente.setOnKeyReleased(e -> {
            lastCliente =
                    (ModeloDataAutocomplet) actfC.getLastSelectedObject();

            if (lastCliente != null) {
                System.out.println(lastCliente.getNameDysplay());

                razonSocial.setText(lastCliente.getNameDysplay());

                String[] data = lastCliente.getOtherData().split(";");

                txtDireccion.setText(data[1]);
                dniRuc.setText(lastCliente.getIdx());

                listar();

            } else {
                btnRegCliente.setDisable(true);
                limpiarFormCliente();
            }
        });
    }

    @FXML
    public void buscarClienteCdni() {

        limpiarFormCliente();

        Stage ventana = StageManager.getPrimaryStage();
        double with = ventana.getMaxWidth() / 2;

        if (autocompCliente.getText().length() == 8
                || autocompCliente.getText().length() == 11) {

            try {
                if (cs.findById(autocompCliente.getText()) != null) {
                    btnRegCliente.setDisable(true);

                    Toast.showToast(
                            ventana, "El cliente si existe",
                            2000, with, 50);

                    return;
                }

                consultarDNIReniec(with);

            } catch (ModelNotFoundException e) {
                btnRegCliente.setDisable(true);

                Toast.showToast(
                        ventana, "El cliente no existe",
                        2000, with, 50);

                consultarDNIReniec(with);
            }

        } else {
            btnRegCliente.setDisable(true);

            Toast.showToast(
                    ventana,
                    "El valor buscado debe tener 8 o 11 digitos",
                    2000, with, 50);
        }
    }

    public void consultarDNIReniec(double with) {

        PersonaDto p = cDni.consultarDNI(autocompCliente.getText());

        if (p != null) {
            razonSocial.setText(
                    p.getNombre() + " "
                            + p.getApellidoPaterno() + " "
                            + p.getApellidoMaterno());

            dniRuc.setText(p.getDni());
            btnRegCliente.setDisable(false);

            Toast.showToast(
                    stage,
                    "El cliente se encontró en RENIEC para registrar debe hacer clik en Add",
                    2000, with, 50);

        } else {
            btnRegCliente.setDisable(true);

            Toast.showToast(
                    stage,
                    "El cliente no se encuentra en RENIEC y debe registrar a través del formulario de cliente",
                    2000, with, 50);
        }
    }

    public void limpiarFormCliente() {
        razonSocial.clear();
        dniRuc.clear();
        txtDireccion.clear();
    }

    @FXML
    public void guardarCliente() {

        Stage ventana = StageManager.getPrimaryStage();
        double with = ventana.getMaxWidth() / 2;

        try {
            Cliente c = Cliente.builder()
                    .dniruc(dniRuc.getText())
                    .nombres(razonSocial.getText())
                    .repLegal(razonSocial.getText())
                    .tipoDocumento(TipoDocumento.DNI)
                    .direccion(txtDireccion.getText())
                    .build();

            cs.save(c);

            btnRegCliente.setDisable(true);

            Toast.showToast(
                    ventana,
                    "El cliente se guardo satisfactoriamente!",
                    2000, with, 50);

            listarCliente();
            listar();

        } catch (Exception e) {
            e.printStackTrace();

            Toast.showToast(
                    ventana, "Error al guardar cliente!",
                    2000, with, 50);
        }
    }

    public void personalizarTabla() {

        TableViewHelper<VentCarrito> tableViewHelper =
                new TableViewHelper<>();

        LinkedHashMap<String, ColumnInfo> columns =
                new LinkedHashMap<>();

        columns.put("ID Prod",
                new ColumnInfo("idProducto.idProducto", 100.0));

        columns.put("Nombre Producto",
                new ColumnInfo("nombreProducto", 300.0));

        columns.put("Cantidad",
                new ColumnInfo("cantidad", 60.0));

        columns.put("P.Unitario",
                new ColumnInfo("punitario", 100.0));

        columns.put("P.Total",
                new ColumnInfo("ptotal", 100.0));

        Consumer<VentCarrito> updateAction =
                ventCarrito ->
                        System.out.println("Actualizar: " + ventCarrito);

        Consumer<VentCarrito> deleteAction =
                ventCarrito -> deleteReg(ventCarrito);

        tableViewHelper.addColumnsInOrderWithSize(
                tableView, columns, updateAction, deleteAction);

        tableView.setTableMenuButtonVisible(true);
    }

    public void listar() {

        tableView.getItems().clear();

        List<VentCarrito> lista =
                daoC.listaCarritoCliente(dniRuc.getText());

        double impoTotal = 0;

        if (lista != null) {
            for (VentCarrito dato : lista) {
                impoTotal += dato.getPtotal();
            }

            tableView.getItems().addAll(lista);
        }

        txtImporteT.setText(String.valueOf(impoTotal));

        double pv = impoTotal / 1.18;

        txtBaseImp.setText(
                String.valueOf(Math.round(pv * 100.0) / 100.0));

        txtIgv.setText(
                String.valueOf(Math.round(pv * 0.18 * 100.0) / 100.0));
    }

    public void editVenCarrito(VentCarrito obj) {
        System.out.println(obj.getDniruc());
    }

    public void deleteReg(VentCarrito obj) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Confirmación");
        alert.setHeaderText("Confirmar acción");
        alert.setContentText(
                "¿Estás seguro de que deseas eliminar este elemento?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            daoC.delete(obj.getIdCarrito());

            Stage ventana = StageManager.getPrimaryStage();
            double with = ventana.getMaxWidth() / 2;

            Toast.showToast(
                    ventana, "¡Acción completada!",
                    2000, with, 50);

            listar();

        } else {
            System.out.println("Acción cancelada");
        }
    }

    @FXML
    private void calcularPT() {

        if (!cantidadPro.getText().equals("")) {

            double dato =
                    Double.parseDouble(punitPro.getText())
                            * Double.parseDouble(cantidadPro.getText());

            preTPro.setText(String.valueOf(dato));

            if (Double.parseDouble(cantidadPro.getText()) > 0.0) {
                btnRegCarrito.setDisable(false);
            } else {
                btnRegCarrito.setDisable(true);
            }

        } else {
            btnRegCarrito.setDisable(true);
        }
    }

    @FXML
    private void registarPCarrito() {

        System.out.println(
                "ID:" + SessionManager.getInstance().getUserId());

        try {
            VentCarrito ss = VentCarrito.builder()
                    .dniruc(dniRuc.getText())
                    .idProducto(
                            ps.findById(Long.parseLong(codigoPro.getText())))
                    .nombreProducto(nombreProducto.getText())
                    .cantidad(Double.parseDouble(cantidadPro.getText()))
                    .punitario(Double.parseDouble(punitPro.getText()))
                    .ptotal(Double.parseDouble(preTPro.getText()))
                    .estado(1)
                    .idUsuario(
                            daoU.findById(
                                    SessionManager.getInstance().getUserId()))
                    .tipoProducto(tipoProducto)
                    .build();

            daoC.save(ss);

            listar();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void registrarVenta() {

        String documentoCliente = dniRuc.getText();

        Stage ventana = StageManager.getPrimaryStage();
        double with = ventana.getMaxWidth() / 2;

        if (documentoCliente == null || documentoCliente.trim().isEmpty()) {
            Toast.showToast(
                    ventana, "Primero selecciona un cliente",
                    2500, with, 50);
            return;
        }

        List<VentCarrito> detallesCarrito =
                daoC.listaCarritoCliente(documentoCliente);

        if (detallesCarrito == null || detallesCarrito.isEmpty()) {
            Toast.showToast(
                    ventana, "No hay productos en el carrito",
                    2500, with, 50);
            return;
        }

        try {
            Venta venta = Venta.builder()
                    .dniruc(cs.findById(documentoCliente))
                    .precioBase(Double.parseDouble(txtBaseImp.getText()))
                    .igv(Double.parseDouble(txtIgv.getText()))
                    .precioTotal(Double.parseDouble(txtImporteT.getText()))
                    .idUsuario(
                            daoU.findById(
                                    SessionManager.getInstance().getUserId()))
                    .serie("V")
                    .tipoDoc("Factura")
                    .fechaGener(LocalDate.now())
                    .numDoc("00")
                    .build();

            Venta ventaGuardada = daoV.save(venta);

            if (ventaGuardada == null || ventaGuardada.getId() == 0) {
                Toast.showToast(
                        ventana, "No se pudo registrar la venta",
                        2500, with, 50);
                return;
            }

            List<VentaDetalle> detallesGuardados = new ArrayList<>();

            for (VentCarrito carrito : detallesCarrito) {

                VentaDetalle detalle = VentaDetalle.builder()
                        .idVenta(ventaGuardada)
                        .idProducto(
                                ps.findById(
                                        carrito.getIdProducto().getIdProducto()))
                        .cantidad(carrito.getCantidad())
                        .descuento(0.0)
                        .pu(carrito.getPunitario())
                        .subtotal(carrito.getPtotal())
                        .build();

                VentaDetalle detalleGuardado = daoVD.save(detalle);

                if (detalleGuardado == null) {
                    throw new IllegalStateException(
                            "No se pudo guardar un detalle de la venta");
                }

                detallesGuardados.add(detalleGuardado);
            }

            ventaGuardada.setDetalleVenta(detallesGuardados);

            // Eliminar los productos del carrito guardado
            daoC.deleteCarAll(documentoCliente);

            // Limpiar el formulario después de registrar la venta
            limpiarFormularioVenta();

            // Mostrar mensaje de confirmación
            Toast.showToast(
                    ventana, "Venta registrada correctamente",
                    3000, with, 50);

        } catch (Exception e) {
            e.printStackTrace();

            Toast.showToast(
                    ventana,
                    "Ocurrió un error al registrar la venta",
                    3000, with, 50);
        }
    }

    private void limpiarFormularioVenta() {

        // Limpiar datos del cliente
        autocompCliente.clear();
        razonSocial.clear();
        dniRuc.clear();
        txtDireccion.clear();

        // Limpiar datos del producto
        autocompProducto.clear();
        nombreProducto.clear();
        codigoPro.clear();
        stockPro.clear();
        cantidadPro.clear();
        punitPro.clear();
        preTPro.clear();

        // Limpiar importes
        txtBaseImp.clear();
        txtIgv.clear();
        txtDescuento.clear();
        txtImporteT.clear();

        // Limpiar tabla del carrito
        tableView.getItems().clear();

        // Reiniciar selecciones
        lastProducto = null;
        lastCliente = null;
        tipoProducto = null;

        // Desactivar botones
        btnRegCarrito.setDisable(true);
        btnRegCliente.setDisable(true);
    }
}
