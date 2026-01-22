package principal;

import utilitaria.SistemaLuxico;
import negocio.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class App extends JFrame {

    private SistemaLuxico sistema;
    private Usuario usuarioActual;

    private CardLayout card;
    private JPanel root;

    private LoginPanel loginPanel;
    private MainPanel mainPanel;

    // Colores y estilos globales
    private final Color COLOR_PRINCIPAL = new Color(135, 206, 250); // Azul celeste claro (light sky blue)
    private final Color COLOR_PRINCIPAL_DARK = new Color(35, 85, 185); // Azul más oscuro para encabezados
    private final Color COLOR_ACCENT = new Color(0, 120, 215);
    private final Color COLOR_ERROR = new Color(160, 0, 0);
    private final Font FONT_TITLE = new Font("Arial", Font.BOLD, 22);
    private final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 13);
    private final Font FONT_BTN = new Font("SansSerif", Font.BOLD, 12);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }

    public App() {
        sistema = new SistemaLuxico();

        setTitle("Luxico - Sistema");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1150, 650);
        setLocationRelativeTo(null);

        card = new CardLayout();
        root = new JPanel(card);

        loginPanel = new LoginPanel();
        mainPanel = new MainPanel();

        root.add(loginPanel, "LOGIN");
        root.add(mainPanel, "MAIN");

        add(root);
        card.show(root, "LOGIN");
        // asegurar foco inicial en el campo usuario
        loginPanel.focusUsuario();
    }

    // =========================================================
    // Helpers de estilo (UI only)
    // =========================================================
    private void styleButton(JButton b, Color bg) {
        b.setBackground(bg != null ? bg : COLOR_ACCENT);
        b.setForeground(Color.WHITE);
        b.setFont(FONT_BTN);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(bg.darker(), 1, true));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
    }

    private void styleButton(JButton b) { styleButton(b, COLOR_ACCENT); }

    private void styleTextField(JTextField t) {
        t.setFont(FONT_LABEL);
        t.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
    }

    private void stylePasswordField(JPasswordField p) {
        p.setFont(FONT_LABEL);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)
        ));
    }

    private void styleTable(JTable t) {
        t.setRowHeight(24);
        t.setFont(new Font("SansSerif", Font.PLAIN, 13));
        t.getTableHeader().setBackground(COLOR_PRINCIPAL_DARK);
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        t.setGridColor(new Color(220, 220, 220));
        // Mejorar alineación y anchos de columnas según encabezado
        styleTableColumns(t);
    }

    // Alinea columnas numéricas a la derecha y textos a la izquierda; sugiere anchos por nombre de columna
    private void styleTableColumns(JTable t) {
        if (t.getColumnModel().getColumnCount() == 0) return;
        javax.swing.table.TableColumnModel cm = t.getColumnModel();
        javax.swing.table.DefaultTableCellRenderer centerHdr = new javax.swing.table.DefaultTableCellRenderer();
        centerHdr.setHorizontalAlignment(SwingConstants.CENTER);
        t.getTableHeader().setDefaultRenderer(centerHdr);

        javax.swing.table.DefaultTableCellRenderer right = new javax.swing.table.DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        javax.swing.table.DefaultTableCellRenderer left = new javax.swing.table.DefaultTableCellRenderer();
        left.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < cm.getColumnCount(); i++) {
            String name = cm.getColumn(i).getHeaderValue().toString().toLowerCase();
            if (name.contains("precio") || name.contains("stock") || name.contains("total") || name.contains("cantidad") || name.contains("cant") || name.equals("id") || name.contains("stockmin")) {
                cm.getColumn(i).setCellRenderer(right);
            } else {
                cm.getColumn(i).setCellRenderer(left);
            }

            // Sugerir anchos manejables
            if (name.contains("codigo") || name.equals("id")) cm.getColumn(i).setPreferredWidth(80);
            else if (name.contains("modelo") || name.contains("cliente") || name.contains("nombre")) cm.getColumn(i).setPreferredWidth(220);
            else if (name.contains("talla") || name.contains("tipo")) cm.getColumn(i).setPreferredWidth(90);
            else if (name.contains("precio") || name.contains("total")) cm.getColumn(i).setPreferredWidth(90);
            else cm.getColumn(i).setPreferredWidth(120);
        }
    }

    private void styleTitledBorder(JComponent c, String title) {
        c.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRINCIPAL_DARK, 1, true),
                title, 0, 0, FONT_LABEL, COLOR_PRINCIPAL_DARK
        ));
    }

    // =========================================================
    // Helpers lógicos (sin cambios)
    // =========================================================
    private Producto buscarProductoPorCodigo(String codigo) {
        if (codigo == null) return null;
        String cod = codigo.trim().toUpperCase();
        if (cod.isEmpty()) return null;

        ArrayList<Producto> list = sistema.getProductos();
        for (Producto p : list) {
            if (p != null && p.getCodigo() != null && p.getCodigo().equals(cod)) return p;
        }
        return null;
    }

    private void logout() {
        usuarioActual = null;
        loginPanel.limpiar();
        card.show(root, "LOGIN");
        // Poner foco en el campo usuario cuando se muestre el login
        loginPanel.focusUsuario();
    }

    // =========================================================
    // LOGIN PANEL (sin popups)
    // =========================================================
    private class LoginPanel extends JPanel {

        private JTextField txtUser;
        private JPasswordField txtPass;
        private JLabel lblMsg;

        public LoginPanel() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            setBackground(COLOR_PRINCIPAL); // Azul celeste claro

            // Contenedor central para centrar contenidos vertical y horizontalmente
            JPanel centerWrapper = new JPanel(new GridBagLayout());
            centerWrapper.setOpaque(false);

            GridBagConstraints cwrap = new GridBagConstraints();
            cwrap.gridx = 0; cwrap.gridy = 0; cwrap.anchor = GridBagConstraints.CENTER;

            // ===== LOGO (escalado proporcional) =====
            JLabel lblLogo = new JLabel();
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            java.net.URL url = getClass().getResource("/recursos/luxico_jordan.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image img = icon.getImage();
                int iw = icon.getIconWidth();
                int ih = icon.getIconHeight();
                // target width adaptativo (no demasiado ancho)
                int targetW = Math.min(520, Math.max(280, iw));
                // mantener proporción
                int targetH = (int) ((double) ih / (double) iw * targetW);
                Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblLogo.setIcon(new ImageIcon(scaled));
            } else {
                lblLogo.setText("LUXICO");
                lblLogo.setForeground(COLOR_PRINCIPAL_DARK);
                lblLogo.setFont(FONT_TITLE);
            }

            // Tarjeta blanca redondeada que contiene el formulario
            JPanel cardPanel = new JPanel(new GridBagLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    int arc = 18;
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    // Sombra sutil
                    g2.setColor(new Color(0, 0, 0, 20));
                    g2.fillRoundRect(6, 6, getWidth() - 6, getHeight() - 6, arc, arc);
                    // Fondo blanco semitransparente
                    g2.setColor(new Color(255, 255, 255, 240));
                    g2.fillRoundRect(0, 0, getWidth() - 12 + 12, getHeight() - 12 + 12, arc, arc);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            cardPanel.setOpaque(false);
            cardPanel.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            JLabel title = new JLabel("Bienvenido a LUXICO", SwingConstants.CENTER);
            title.setFont(new Font("Arial", Font.BOLD, 20));
            title.setForeground(COLOR_PRINCIPAL_DARK);
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            cardPanel.add(title, gbc);

            gbc.gridwidth = 1;
            gbc.gridy = 1; gbc.gridx = 0;
            JLabel lblU = new JLabel("Usuario:"); lblU.setFont(FONT_LABEL);
            cardPanel.add(lblU, gbc);
            txtUser = new JTextField(18);
            styleTextField(txtUser);
            gbc.gridx = 1; gbc.gridy = 1;
            cardPanel.add(txtUser, gbc);

            gbc.gridy = 2; gbc.gridx = 0;
            JLabel lblP = new JLabel("Clave:"); lblP.setFont(FONT_LABEL);
            cardPanel.add(lblP, gbc);
            txtPass = new JPasswordField(18);
            stylePasswordField(txtPass);
            gbc.gridx = 1; gbc.gridy = 2;
            cardPanel.add(txtPass, gbc);

            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            row.setOpaque(false);
            JButton btnLogin = new JButton("Ingresar");
            styleButton(btnLogin, COLOR_PRINCIPAL_DARK);
            btnLogin.setPreferredSize(new Dimension(160, 34));
            row.add(btnLogin);

            JButton btnHelp = new JButton("Ayuda");
            styleButton(btnHelp, new Color(120, 120, 120));
            btnHelp.setPreferredSize(new Dimension(90, 34));
            row.add(btnHelp);

            cardPanel.add(row, gbc);

            lblMsg = new JLabel(" ", SwingConstants.CENTER);
            lblMsg.setForeground(COLOR_ERROR);
            gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
            cardPanel.add(lblMsg, gbc);

            // Evento login (misma lógica que antes)
            btnLogin.addActionListener(e -> {
                String u = txtUser.getText().trim();
                String p = new String(txtPass.getPassword()).trim();

                if (u.isEmpty() || p.isEmpty()) {
                    setMsg("Completa usuario y clave.");
                    return;
                }

                Usuario log = sistema.login(u, p);
                if (log == null) {
                    setMsg("Credenciales incorrectas.");
                    return;
                }

                usuarioActual = log;
                setMsg(" ");

                mainPanel.configurarPorRol(usuarioActual);
                mainPanel.refrescarTodo();
                mainPanel.setStatus("Sesión iniciada: " + usuarioActual.getUsuario());

                App.this.card.show(App.this.root, "MAIN");
            });

            // Ayuda: mostrar mensaje informativo
            btnHelp.addActionListener(e -> setMsg("Contacto: profesor@ejemplo.edu / user: admin, pass: admin123"));

            // Componer el wrapper: logo arriba y tarjeta con formulario centrada
            JPanel box = new JPanel();
            box.setOpaque(false);
            box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(lblLogo);
            box.add(Box.createVerticalStrut(12));
            cardPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            box.add(cardPanel);

            centerWrapper.add(box, cwrap);
            add(centerWrapper, BorderLayout.CENTER);
        }

        public void setMsg(String msg) { lblMsg.setText(msg); }

        public void limpiar() {
            txtUser.setText("");
            txtPass.setText("");
            setMsg(" ");
        }

        // pedir foco en el campo usuario (usar invokeLater para garantizar que la ventana esté visible)
        public void focusUsuario() {
            SwingUtilities.invokeLater(() -> {
                if (txtUser != null) txtUser.requestFocusInWindow();
            });
        }
    }

    // =========================================================
    // MAIN PANEL
    // =========================================================
    private class MainPanel extends JPanel {

        private JLabel lblRol;
        private JLabel lblStatus;
        private JTabbedPane tabs;

        private ProductosPanel productosPanel;
        private MovimientosPanel movimientosPanel;
        private ClientesPanel clientesPanel;
        private PedidosPanel pedidosPanel;
        private ReportesPanel reportesPanel;

        public MainPanel() {
            setLayout(new BorderLayout());

            JPanel top = new JPanel(new BorderLayout());
            top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            top.setBackground(COLOR_PRINCIPAL_DARK); // Encabezado oscuro

            lblRol = new JLabel("Usuario: - | Rol: -");
            lblRol.setFont(new Font("Arial", Font.BOLD, 16));
            lblRol.setForeground(Color.WHITE);

            JButton btnLogout = new JButton("Cerrar sesión");
            styleButton(btnLogout, new Color(200, 50, 50));
            btnLogout.addActionListener(e -> logout());

            top.add(lblRol, BorderLayout.WEST);
            top.add(btnLogout, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            tabs = new JTabbedPane();
            tabs.setBackground(COLOR_PRINCIPAL);
            tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));

            lblStatus = new JLabel("Listo.");
            lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
            add(lblStatus, BorderLayout.SOUTH);

            // Crear paneles
            productosPanel = new ProductosPanel(this);
            movimientosPanel = new MovimientosPanel(this);
            clientesPanel = new ClientesPanel(this);
            pedidosPanel = new PedidosPanel(this);
            reportesPanel = new ReportesPanel(this);

            add(tabs, BorderLayout.CENTER);

            // Refrescar automáticamente al cambiar de pestaña
            tabs.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    Component c = tabs.getSelectedComponent();
                    if (c instanceof ProductosPanel) ((ProductosPanel) c).refrescarTabla();
                    else if (c instanceof MovimientosPanel) ((MovimientosPanel) c).refrescarTabla();
                    else if (c instanceof ClientesPanel) ((ClientesPanel) c).refrescarTabla();
                    else if (c instanceof PedidosPanel) ((PedidosPanel) c).refrescarTabla();
                    // Reportes no necesita refrescar tabla
                }
            });

            setStatus("Listo.");
        }

        public void setStatus(String msg) {
            if (lblStatus != null) lblStatus.setText(msg);
        }

        public void configurarPorRol(Usuario u) {
            String rol;
            if (u instanceof Admin) rol = "ADMIN";
            else if (u instanceof Bodega) rol = "BODEGA";
            else if (u instanceof Ventas) rol = "VENTAS";
            else rol = "DESCONOCIDO";

            lblRol.setText("Usuario: " + u.getUsuario() + " | Rol: " + rol);

            tabs.removeAll();

            if (u instanceof Admin) {
                tabs.addTab("Productos", productosPanel);
                tabs.addTab("Movimientos", movimientosPanel);
                tabs.addTab("Clientes", clientesPanel);
                tabs.addTab("Pedidos", pedidosPanel);
                tabs.addTab("Reportes", reportesPanel);
            } else if (u instanceof Bodega) {
                tabs.addTab("Productos", productosPanel);
                tabs.addTab("Movimientos", movimientosPanel);
                tabs.addTab("Pedidos", pedidosPanel);
                tabs.addTab("Reportes", reportesPanel);
            } else if (u instanceof Ventas) {
                tabs.addTab("Clientes", clientesPanel);
                tabs.addTab("Pedidos", pedidosPanel);
                tabs.addTab("Reportes", reportesPanel);
            }
        }

        public void refrescarTodo() {
            productosPanel.refrescarTabla();
            movimientosPanel.refrescarTabla();
            clientesPanel.refrescarTabla();
            pedidosPanel.refrescarTabla();
        }
    }

    // =========================================================
    // PRODUCTOS
    // =========================================================
    private class ProductosPanel extends JPanel {

        private final MainPanel parent;

        private DefaultTableModel model;
        private JTable table;

        private JTextField txtCodigo, txtModelo, txtTalla, txtPrecio, txtStock, txtStockMin;
        private JComboBox<String> cbTipo;
        private JLabel lblMsg;

        private JButton btnNuevo, btnGuardar, btnEliminar, btnLimpiar; // quitado btnRefrescar

        private boolean modoEdicion = false;
        private Producto productoEditando = null;

        public ProductosPanel(MainPanel parent) {
            this.parent = parent;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(COLOR_PRINCIPAL);

            model = new DefaultTableModel(
                    new Object[]{"Código", "Modelo", "Talla", "Precio", "Stock", "StockMin", "Tipo"}, 0
            ) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            styleTable(table);
            JScrollPane spTable = new JScrollPane(table);

            JPanel form = new JPanel(new GridBagLayout());
            styleTitledBorder(form, "Formulario Producto");
            form.setBackground(new Color(255, 255, 255, 220));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            txtCodigo = new JTextField(16);
            txtModelo = new JTextField(16);
            txtTalla = new JTextField(16);
            txtPrecio = new JTextField(16);
            txtStock = new JTextField(16);
            txtStockMin = new JTextField(16);
            cbTipo = new JComboBox<>(new String[]{"REGULAR", "EDICION_LIMITADA"});

            styleTextField(txtCodigo); styleTextField(txtModelo); styleTextField(txtTalla);
            styleTextField(txtPrecio); styleTextField(txtStock); styleTextField(txtStockMin);
            cbTipo.setFont(FONT_LABEL);

            int y = 0;
            addRow(form, gbc, y++, "Código:", txtCodigo);
            addRow(form, gbc, y++, "Modelo:", txtModelo);
            addRow(form, gbc, y++, "Talla:", txtTalla);
            addRow(form, gbc, y++, "Precio:", txtPrecio);
            addRow(form, gbc, y++, "Stock:", txtStock);
            addRow(form, gbc, y++, "Stock mínimo:", txtStockMin);
            addRow(form, gbc, y++, "Tipo:", cbTipo);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            acciones.setOpaque(false);
            btnNuevo = new JButton("Nuevo");
            btnGuardar = new JButton("Guardar");
            btnEliminar = new JButton("Eliminar (selección)");
            btnLimpiar = new JButton("Limpiar");

            styleButton(btnNuevo, COLOR_PRINCIPAL_DARK);
            styleButton(btnGuardar, COLOR_ACCENT);
            styleButton(btnEliminar, new Color(200, 50, 50));
            styleButton(btnLimpiar, new Color(120, 120, 120));

            acciones.add(btnNuevo);
            acciones.add(btnGuardar);
            acciones.add(btnEliminar);
            acciones.add(btnLimpiar);

            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
            form.add(acciones, gbc);

            lblMsg = new JLabel(" ");
            lblMsg.setForeground(COLOR_ERROR);
            gbc.gridx = 0; gbc.gridy = y + 1; gbc.gridwidth = 2;
            form.add(lblMsg, gbc);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, spTable, form);
            split.setResizeWeight(0.65);
            add(split, BorderLayout.CENTER);

            // No hay botón "Refrescar". Las tablas se actualizan automáticamente al cambiar pestaña
            btnLimpiar.addActionListener(e -> limpiarForm());
            btnNuevo.addActionListener(e -> nuevo());
            btnGuardar.addActionListener(e -> guardar());
            btnEliminar.addActionListener(e -> eliminarSeleccion());

            table.getSelectionModel().addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) return;
                cargarSeleccion();
            });

            refrescarTabla();
        }

        private void setMsg(String msg) {
            lblMsg.setText(msg);
            if (parent != null) parent.setStatus(msg);
        }

        private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent comp) {
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = y;
            JLabel l = new JLabel(label); l.setFont(FONT_LABEL);
            form.add(l, gbc);
            gbc.gridx = 1; gbc.gridy = y;
            form.add(comp, gbc);
        }

        private boolean puedeEditar() {
            if (!(usuarioActual instanceof Admin) && !(usuarioActual instanceof Bodega)) {
                setMsg("Acceso denegado: solo Admin/Bodega puede modificar productos.");
                return false;
            }
            return true;
        }

        public void refrescarTabla() {
            model.setRowCount(0);
            ArrayList<Producto> list = sistema.getProductos();
            for (Producto p : list) {
                model.addRow(new Object[]{
                        p.getCodigo(), p.getModelo(), p.getTalla(),
                        p.getPrecio(), p.getStock(), p.getStockMin(), p.getTipo()
                });
            }
            setMsg("Productos actualizados.");
        }

        private void limpiarForm() {
            txtCodigo.setText("");
            txtModelo.setText("");
            txtTalla.setText("");
            txtPrecio.setText("");
            txtStock.setText("");
            txtStockMin.setText("");
            cbTipo.setSelectedIndex(0);

            modoEdicion = false;
            productoEditando = null;
            txtCodigo.setEnabled(true);

            setMsg("Formulario limpio (modo crear).");
        }

        private void nuevo() {
            if (!puedeEditar()) return;
            limpiarForm();
            setMsg("Modo CREAR: llena el formulario y presiona Guardar.");
        }

        private void cargarSeleccion() {
            int row = table.getSelectedRow();
            if (row < 0) return;

            String codigo = String.valueOf(model.getValueAt(row, 0));
            Producto p = buscarProductoPorCodigo(codigo);
            if (p == null) return;

            productoEditando = p;
            modoEdicion = true;

            txtCodigo.setText(p.getCodigo());
            txtModelo.setText(p.getModelo());
            txtTalla.setText(p.getTalla());
            txtPrecio.setText(String.valueOf(p.getPrecio()));
            txtStock.setText(String.valueOf(p.getStock()));
            txtStockMin.setText(String.valueOf(p.getStockMin()));

            String tipo = p.getTipo();
            if (tipo != null && tipo.toLowerCase().contains("limit")) cbTipo.setSelectedItem("EDICION_LIMITADA");
            else cbTipo.setSelectedItem("REGULAR");

            txtCodigo.setEnabled(false);

            setMsg("Modo EDITAR: cambia campos y presiona Guardar.");
        }

        private void guardar() {
            if (!puedeEditar()) return;

            String codigo = txtCodigo.getText().trim().toUpperCase();
            String modelo = txtModelo.getText().trim();
            String talla = txtTalla.getText().trim();

            if (codigo.isEmpty() || modelo.isEmpty() || talla.isEmpty()) {
                setMsg("Error: código/modelo/talla no pueden estar vacíos.");
                return;
            }

            double precio;
            int stock, stockMin;

            try {
                precio = Double.parseDouble(txtPrecio.getText().trim());
                stock = Integer.parseInt(txtStock.getText().trim());
                stockMin = Integer.parseInt(txtStockMin.getText().trim());
            } catch (Exception ex) {
                setMsg("Error: precio/stock/stockMin deben ser números válidos.");
                return;
            }

            if (precio <= 0 || stock < 0 || stockMin < 0) {
                setMsg("Error: precio > 0 y stock/stockMin >= 0.");
                return;
            }

            String tipoSel = String.valueOf(cbTipo.getSelectedItem());

            if (!modoEdicion) {
                if (buscarProductoPorCodigo(codigo) != null) {
                    setMsg("Error: ya existe un producto con ese código.");
                    return;
                }

                Producto nuevo = tipoSel.equals("EDICION_LIMITADA")
                        ? new ProductoEdicionLimitada(codigo, modelo, talla, precio, stock, stockMin)
                        : new ProductoRegular(codigo, modelo, talla, precio, stock, stockMin);

                boolean ok = sistema.agregarProducto(nuevo);
                if (ok) {
                    refrescarTabla();
                    limpiarForm();
                    setMsg("Producto creado: " + codigo);
                } else {
                    setMsg("No se pudo crear el producto.");
                }
                return;
            }

            if (productoEditando == null) {
                setMsg("Selecciona un producto en la tabla para editar.");
                return;
            }

            productoEditando.setModelo(modelo);
            productoEditando.setTalla(talla);
            productoEditando.setPrecio(precio);

            // ⚠️ SOLO SI EXISTE setStock EN TU CLASE Producto
            // Si no existe, comenta esta línea:
            // productoEditando.setStock(stock);

            productoEditando.setStockMin(stockMin);

            refrescarTabla();
            setMsg("Producto actualizado: " + productoEditando.getCodigo());
        }

        private void eliminarSeleccion() {
            if (!puedeEditar()) return;

            int row = table.getSelectedRow();
            if (row < 0) {
                setMsg("Selecciona un producto en la tabla para eliminar.");
                return;
            }

            String codigo = String.valueOf(model.getValueAt(row, 0));
            Producto p = buscarProductoPorCodigo(codigo);
            if (p == null) {
                setMsg("No se encontró el producto.");
                return;
            }

            boolean ok = sistema.eliminarProducto(p);
            if (ok) {
                refrescarTabla();
                limpiarForm();
                setMsg("Producto eliminado: " + codigo);
            } else {
                setMsg("No se pudo eliminar: " + codigo);
            }
        }
    }

    // =========================================================
    // MOVIMIENTOS
    // =========================================================
    private class MovimientosPanel extends JPanel {

        private final MainPanel parent;

        private DefaultTableModel model;
        private JTable table;

        private JTextField txtCodProd, txtCantidad, txtMotivo;
        private JComboBox<String> cbTipo;
        private JLabel lblMsg;

        public MovimientosPanel(MainPanel parent) {
            this.parent = parent;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(COLOR_PRINCIPAL);

            model = new DefaultTableModel(new Object[]{"Producto", "Tipo", "Cantidad", "Fecha", "Motivo"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            styleTable(table);

            JScrollPane sp = new JScrollPane(table);

            JPanel form = new JPanel(new GridBagLayout());
            styleTitledBorder(form, "Registrar Movimiento");
            form.setBackground(new Color(255, 255, 255, 220));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            txtCodProd = new JTextField(16);
            cbTipo = new JComboBox<>(new String[]{"ENTRADA", "SALIDA", "DEVOLUCION"});
            txtCantidad = new JTextField(16);
            txtMotivo = new JTextField(16);

            styleTextField(txtCodProd); styleTextField(txtCantidad); styleTextField(txtMotivo);
            cbTipo.setFont(FONT_LABEL);

            int y = 0;
            addRow(form, gbc, y++, "Código producto:", txtCodProd);
            addRow(form, gbc, y++, "Tipo:", cbTipo);
            addRow(form, gbc, y++, "Cantidad:", txtCantidad);
            addRow(form, gbc, y++, "Motivo:", txtMotivo);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            acciones.setOpaque(false);
            JButton btnRegistrar = new JButton("Registrar");
            JButton btnLimpiar = new JButton("Limpiar");
            styleButton(btnRegistrar, COLOR_ACCENT);
            styleButton(btnLimpiar, new Color(120, 120, 120));
            acciones.add(btnRegistrar);
            acciones.add(btnLimpiar);

            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
            form.add(acciones, gbc);

            lblMsg = new JLabel(" ");
            lblMsg.setForeground(COLOR_ERROR);
            gbc.gridx = 0; gbc.gridy = y + 1; gbc.gridwidth = 2;
            form.add(lblMsg, gbc);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, form);
            split.setResizeWeight(0.7);
            add(split, BorderLayout.CENTER);

            btnRegistrar.addActionListener(e -> registrar());
            btnLimpiar.addActionListener(e -> limpiar());

            refrescarTabla();
        }

        private void setMsg(String msg) {
            lblMsg.setText(msg);
            if (parent != null) parent.setStatus(msg);
        }

        private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent comp) {
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = y;
            JLabel l = new JLabel(label); l.setFont(FONT_LABEL);
            form.add(l, gbc);
            gbc.gridx = 1; gbc.gridy = y;
            form.add(comp, gbc);
        }

        private boolean puedeRegistrar() {
            if (!(usuarioActual instanceof Admin) && !(usuarioActual instanceof Bodega)) {
                setMsg("Acceso denegado: solo Admin/Bodega puede registrar movimientos.");
                return false;
            }
            return true;
        }

        private void limpiar() {
            txtCodProd.setText("");
            txtCantidad.setText("");
            txtMotivo.setText("");
            cbTipo.setSelectedIndex(0);
            setMsg("Formulario limpio.");
        }

        public void refrescarTabla() {
            model.setRowCount(0);
            ArrayList<MovimientoInventario> list = sistema.getMovimientos();
            for (MovimientoInventario m : list) {
                Producto p = m.getProducto();
                String cod = (p != null) ? p.getCodigo() : "N/A";
                model.addRow(new Object[]{cod, m.getTipo(), m.getCantidad(), m.getFecha(), m.getMotivo()});
            }
            setMsg("Movimientos actualizados.");
        }

        private void registrar() {
            if (!puedeRegistrar()) return;

            String cod = txtCodProd.getText().trim().toUpperCase();
            if (cod.isEmpty()) { setMsg("Código vacío."); return; }

            Producto prod = buscarProductoPorCodigo(cod);
            if (prod == null) { setMsg("Producto no encontrado: " + cod); return; }

            int cantidad;
            try {
                cantidad = Integer.parseInt(txtCantidad.getText().trim());
            } catch (Exception ex) {
                setMsg("Cantidad inválida.");
                return;
            }
            if (cantidad <= 0) { setMsg("Cantidad debe ser > 0."); return; }

            String motivo = txtMotivo.getText().trim();
            if (motivo.isEmpty()) { setMsg("Motivo no puede estar vacío."); return; }

            String fecha = LocalDate.now().toString();
            TipoMovimiento tipo = TipoMovimiento.valueOf(String.valueOf(cbTipo.getSelectedItem()));

            boolean ok = sistema.registrarMovimiento(prod, tipo, cantidad, fecha, motivo);
            if (ok) {
                refrescarTabla();
                limpiar();
                setMsg("Movimiento registrado.");
            } else {
                setMsg("No se pudo registrar el movimiento.");
            }
        }
    }

    // =========================================================
    // CLIENTES
    // =========================================================
    private class ClientesPanel extends JPanel {

        private final MainPanel parent;

        private DefaultTableModel model;
        private JTable table;

        private JTextField txtCedula, txtNombre, txtDir, txtTel, txtCorreo;
        private JLabel lblMsg;

        private JButton btnNuevo, btnGuardar, btnEliminarSel, btnLimpiar; // quitado btnRefrescar

        private boolean modoEdicion = false;
        private Cliente clienteEditando = null;

        public ClientesPanel(MainPanel parent) {
            this.parent = parent;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(COLOR_PRINCIPAL);

            model = new DefaultTableModel(new Object[]{"Cédula", "Nombre", "Dirección", "Teléfono", "Correo"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            styleTable(table);
            JScrollPane sp = new JScrollPane(table);

            JPanel form = new JPanel(new GridBagLayout());
            styleTitledBorder(form, "Formulario Cliente");
            form.setBackground(new Color(255, 255, 255, 220));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6, 6, 6, 6);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            txtCedula = new JTextField(16);
            txtNombre = new JTextField(16);
            txtDir = new JTextField(16);
            txtTel = new JTextField(16);
            txtCorreo = new JTextField(16);

            styleTextField(txtCedula); styleTextField(txtNombre); styleTextField(txtDir);
            styleTextField(txtTel); styleTextField(txtCorreo);

            int y = 0;
            addRow(form, gbc, y++, "Cédula:", txtCedula);
            addRow(form, gbc, y++, "Nombre:", txtNombre);
            addRow(form, gbc, y++, "Dirección:", txtDir);
            addRow(form, gbc, y++, "Teléfono:", txtTel);
            addRow(form, gbc, y++, "Correo:", txtCorreo);

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            acciones.setOpaque(false);
            btnNuevo = new JButton("Nuevo");
            btnGuardar = new JButton("Guardar");
            btnEliminarSel = new JButton("Eliminar (selección)");
            btnLimpiar = new JButton("Limpiar");

            styleButton(btnNuevo, COLOR_PRINCIPAL_DARK);
            styleButton(btnGuardar, COLOR_ACCENT);
            styleButton(btnEliminarSel, new Color(200, 50, 50));
            styleButton(btnLimpiar, new Color(120, 120, 120));

            acciones.add(btnNuevo);
            acciones.add(btnGuardar);
            acciones.add(btnEliminarSel);
            acciones.add(btnLimpiar);

            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
            form.add(acciones, gbc);

            lblMsg = new JLabel(" ");
            lblMsg.setForeground(COLOR_ERROR);
            gbc.gridx = 0; gbc.gridy = y + 1; gbc.gridwidth = 2;
            form.add(lblMsg, gbc);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, form);
            split.setResizeWeight(0.7);
            add(split, BorderLayout.CENTER);

            btnLimpiar.addActionListener(e -> limpiarForm());
            btnNuevo.addActionListener(e -> nuevo());
            btnGuardar.addActionListener(e -> guardar());
            btnEliminarSel.addActionListener(e -> eliminarSeleccion());

            table.getSelectionModel().addListSelectionListener(e -> {
                if (e.getValueIsAdjusting()) return;
                cargarSeleccion();
            });

            refrescarTabla();
        }

        private void setMsg(String msg) {
            lblMsg.setText(msg);
            if (parent != null) parent.setStatus(msg);
        }

        private void addRow(JPanel form, GridBagConstraints gbc, int y, String label, JComponent comp) {
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = y;
            JLabel l = new JLabel(label); l.setFont(FONT_LABEL);
            form.add(l, gbc);
            gbc.gridx = 1; gbc.gridy = y;
            form.add(comp, gbc);
        }

        private boolean puedeEditar() {
            if (!(usuarioActual instanceof Admin) && !(usuarioActual instanceof Ventas)) {
                setMsg("Acceso denegado: solo Admin/Ventas puede gestionar clientes.");
                return false;
            }
            return true;
        }

        public void refrescarTabla() {
            model.setRowCount(0);
            ArrayList<Cliente> list = sistema.getClientes();
            for (Cliente c : list) {
                model.addRow(new Object[]{c.getCedula(), c.getNombre(), c.getDireccion(), c.getTelefono(), c.getCorreo()});
            }
            setMsg("Clientes actualizados.");
        }

        private void limpiarForm() {
            txtCedula.setText("");
            txtNombre.setText("");
            txtDir.setText("");
            txtTel.setText("");
            txtCorreo.setText("");
            txtCedula.setEnabled(true);

            modoEdicion = false;
            clienteEditando = null;

            setMsg("Formulario limpio (modo crear).");
        }

        private void nuevo() {
            if (!puedeEditar()) return;
            limpiarForm();
            setMsg("Modo CREAR cliente.");
        }

        private void cargarSeleccion() {
            int row = table.getSelectedRow();
            if (row < 0) return;

            String cedula = String.valueOf(model.getValueAt(row, 0));
            Cliente c = sistema.buscarCliente(cedula);
            if (c == null) return;

            clienteEditando = c;
            modoEdicion = true;

            txtCedula.setText(c.getCedula());
            txtNombre.setText(c.getNombre());
            txtDir.setText(c.getDireccion());
            txtTel.setText(c.getTelefono());
            txtCorreo.setText(c.getCorreo());

            txtCedula.setEnabled(false);

            setMsg("Modo EDITAR cliente.");
        }

        private void guardar() {
            if (!puedeEditar()) return;

            String ced = txtCedula.getText().trim();
            String nom = txtNombre.getText().trim();
            String dir = txtDir.getText().trim();
            String tel = txtTel.getText().trim();
            String cor = txtCorreo.getText().trim();

            if (ced.isEmpty() || nom.isEmpty()) { setMsg("Cédula y nombre son obligatorios."); return; }

            if (!modoEdicion) {
                Cliente nuevo = new Cliente(ced, nom, dir, tel, cor);
                boolean ok = sistema.registrarCliente(nuevo);
                if (ok) {
                    refrescarTabla();
                    limpiarForm();
                    setMsg("Cliente registrado: " + ced);
                } else {
                    setMsg("No se pudo registrar el cliente.");
                }
                return;
            }

            if (clienteEditando == null) { setMsg("Selecciona un cliente."); return; }

            boolean ok = sistema.editarCliente(ced, nom, dir, tel, cor);
            if (ok) {
                refrescarTabla();
                setMsg("Cliente actualizado: " + ced);
            } else {
                setMsg("No se pudo actualizar el cliente.");
            }
        }

        private void eliminarSeleccion() {
            if (!puedeEditar()) return;

            int row = table.getSelectedRow();
            if (row < 0) { setMsg("Selecciona un cliente para eliminar."); return; }

            String cedula = String.valueOf(model.getValueAt(row, 0));
            boolean ok = sistema.eliminarCliente(cedula);
            if (ok) {
                refrescarTabla();
                limpiarForm();
                setMsg("Cliente eliminado: " + cedula);
            } else {
                setMsg("No se pudo eliminar el cliente.");
            }
        }
    }

    // =========================================================
    // PEDIDOS (sin popups)
    // =========================================================
    private class PedidosPanel extends JPanel {

        private final MainPanel parent;

        private DefaultTableModel model;
        private JTable table;

        private JTextField txtCedulaCrear;
        private JTextField txtCodItem, txtCantItem;
        private JComboBox<String> cbEstado;

        private JTextArea areaDetalle;
        private JLabel lblMsg;

        public PedidosPanel(MainPanel parent) {
            this.parent = parent;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(COLOR_PRINCIPAL);

            model = new DefaultTableModel(new Object[]{"ID", "Cliente", "Cedula", "Fecha", "Estado", "Total"}, 0) {
                public boolean isCellEditable(int r, int c) { return false; }
            };
            table = new JTable(model);
            styleTable(table);
            JScrollPane sp = new JScrollPane(table);

            JPanel right = new JPanel();
            right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
            right.setOpaque(false);

            right.add(panelCrearYAgregar());
            right.add(Box.createVerticalStrut(10));
            right.add(panelConfirmar());
            right.add(Box.createVerticalStrut(10));
            right.add(panelCambiarEstado());
            right.add(Box.createVerticalStrut(10));
            right.add(panelPreparar());
            right.add(Box.createVerticalStrut(10));

            JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            acciones.setOpaque(false);
            JButton btnVerDetalle = new JButton("Ver detalle (selección)");
            styleButton(btnVerDetalle, COLOR_PRINCIPAL_DARK);
            acciones.add(btnVerDetalle);
            right.add(acciones);

            lblMsg = new JLabel(" ");
            lblMsg.setForeground(COLOR_ERROR);
            right.add(lblMsg);

            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, right);
            split.setResizeWeight(0.7);

            areaDetalle = new JTextArea(8, 80);
            areaDetalle.setFont(new Font("Consolas", Font.PLAIN, 12));
            areaDetalle.setEditable(false);
            JScrollPane spDetalle = new JScrollPane(areaDetalle);
            spDetalle.setBorder(BorderFactory.createTitledBorder("Detalle del pedido"));

            add(split, BorderLayout.CENTER);
            add(spDetalle, BorderLayout.SOUTH);

            // quitar boton Refrescar: actualizamos automáticamente al cambiar pestaña
            btnVerDetalle.addActionListener(e -> verDetalleSeleccion());

            refrescarTabla();
        }

        private void setMsg(String msg) {
            lblMsg.setText(msg);
            if (parent != null) parent.setStatus(msg);
        }

        public void refrescarTabla() {
            model.setRowCount(0);
            ArrayList<Pedido> list = sistema.getPedidos();
            for (Pedido p : list) {
                Cliente c = p.getCliente();
                String nombre = (c != null) ? c.getNombre() : "N/A";
                String cedula = (c != null) ? c.getCedula() : "N/A";

                model.addRow(new Object[]{
                        p.getId(), nombre, cedula, p.getFechaCreacion(), p.getEstado(), p.getTotal()
                });
            }
            setMsg("Pedidos actualizados.");
        }

        private boolean puedeVentas() {
            return (usuarioActual instanceof Admin) || (usuarioActual instanceof Ventas);
        }

        private boolean puedeBodega() {
            return (usuarioActual instanceof Admin) || (usuarioActual instanceof Bodega);
        }

        private JPanel panelCrearYAgregar() {
            JPanel p = new JPanel(new GridBagLayout());
            styleTitledBorder(p, "Crear Pedido + Agregar Producto");
            p.setBackground(new Color(255, 255, 255, 220));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4, 4, 4, 4);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1;

            txtCedulaCrear = new JTextField(12);
            txtCodItem = new JTextField(12);
            txtCantItem = new JTextField(6);
            styleTextField(txtCedulaCrear); styleTextField(txtCodItem); styleTextField(txtCantItem);

            JButton btnCrearYAniadir = new JButton("Crear pedido y añadir producto");
            styleButton(btnCrearYAniadir, COLOR_PRINCIPAL_DARK);

            int y = 0;
            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
            JLabel l1 = new JLabel("Cédula cliente:"); l1.setFont(FONT_LABEL);
            p.add(l1, gbc);
            gbc.gridx = 1; gbc.gridy = y++;
            p.add(txtCedulaCrear, gbc);

            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
            JLabel l2 = new JLabel("Código prod:"); l2.setFont(FONT_LABEL);
            p.add(l2, gbc);
            gbc.gridx = 1; gbc.gridy = y++;
            p.add(txtCodItem, gbc);

            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 1;
            JLabel l3 = new JLabel("Cantidad:"); l3.setFont(FONT_LABEL);
            p.add(l3, gbc);
            gbc.gridx = 1; gbc.gridy = y++;
            p.add(txtCantItem, gbc);

            gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
            p.add(btnCrearYAniadir, gbc);

            btnCrearYAniadir.addActionListener(e -> {
                if (!puedeVentas()) { setMsg("Solo Admin/Ventas puede crear pedidos."); return; }

                String ced = txtCedulaCrear.getText().trim();
                String cod = txtCodItem.getText().trim().toUpperCase();
                int cant;
                try { cant = Integer.parseInt(txtCantItem.getText().trim()); }
                catch (Exception ex) { setMsg("Cantidad inválida."); return; }

                if (ced.isEmpty()) { setMsg("Cédula vacía."); return; }
                if (cod.isEmpty()) { setMsg("Código de producto vacío."); return; }
                if (cant <= 0) { setMsg("Cantidad debe ser > 0."); return; }

                Cliente c = sistema.buscarCliente(ced);
                if (c == null) { setMsg("Cliente no encontrado: " + ced); return; }

                Producto prod = buscarProductoPorCodigo(cod);
                if (prod == null) { setMsg("Producto no encontrado: " + cod); return; }

                String fecha = LocalDate.now().toString();
                int id = sistema.crearPedido(c, fecha, prod, cant);

                if (id > 0) {
                    refrescarTabla();
                    txtCedulaCrear.setText(""); txtCodItem.setText(""); txtCantItem.setText("");
                    setMsg("Pedido creado y producto agregado. ID: " + id);
                } else {
                    setMsg("No se pudo crear el pedido o agregar el producto (revisa stock/validaciones).");
                }
            });


            return p;
        }

        private JPanel panelConfirmar() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            styleTitledBorder(p, "Confirmar Pedido (selección)");
            p.setBackground(new Color(255, 255, 255, 220));

            JButton btnConfirmar = new JButton("Confirmar pedido (selección)");
            styleButton(btnConfirmar, COLOR_PRINCIPAL_DARK);
            JButton btnEliminarPedido = new JButton("Eliminar pedido (selección)");
            styleButton(btnEliminarPedido, new Color(200, 50, 50));

            p.add(btnConfirmar);
            p.add(btnEliminarPedido);

            btnConfirmar.addActionListener(e -> {
                if (!puedeVentas()) { setMsg("Solo Admin/Ventas puede confirmar."); return; }
                int row = table.getSelectedRow();
                if (row < 0) { setMsg("Selecciona un pedido en la tabla."); return; }
                int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
                boolean ok = sistema.confirmarPedido(id);
                if (ok) { refrescarTabla(); setMsg("Pedido confirmado: " + id); }
                else setMsg("No se pudo confirmar (revisa estado/stock).");
            });

            btnEliminarPedido.addActionListener(e -> {
                if (!(usuarioActual instanceof Admin)) { setMsg("Solo Admin puede eliminar pedidos."); return; }
                int row = table.getSelectedRow();
                if (row < 0) { setMsg("Selecciona un pedido en la tabla."); return; }
                int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
                boolean ok = sistema.eliminarPedido(id);
                if (ok) { refrescarTabla(); setMsg("Pedido eliminado: " + id); }
                else setMsg("No se pudo eliminar el pedido.");
            });

            return p;
        }

        private JPanel panelCambiarEstado() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            styleTitledBorder(p, "Cambiar Estado (selección)");
            p.setBackground(new Color(255, 255, 255, 220));

            cbEstado = new JComboBox<>(new String[]{"EN_PREPARACION", "ENVIADO"});
            cbEstado.setFont(FONT_LABEL);
            JButton btnCambiar = new JButton("Cambiar estado (selección)");
            styleButton(btnCambiar, COLOR_ACCENT);

            p.add(cbEstado);
            p.add(btnCambiar);

            btnCambiar.addActionListener(e -> {
                if (!puedeVentas()) { setMsg("Solo Admin/Ventas puede cambiar estado."); return; }
                int row = table.getSelectedRow();
                if (row < 0) { setMsg("Selecciona un pedido en la tabla."); return; }
                int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
                EstadoPedido est = EstadoPedido.valueOf(String.valueOf(cbEstado.getSelectedItem()));
                boolean ok = sistema.cambiarEstadoPedido(id, est);
                if (ok) { refrescarTabla(); setMsg("Estado actualizado para pedido: " + id); }
                else setMsg("No se pudo cambiar el estado.");
            });

            return p;
        }

        private JPanel panelPreparar() {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            styleTitledBorder(p, "Preparar (selección)");
            p.setBackground(new Color(255, 255, 255, 220));

            JButton btnPreparar = new JButton("Marcar EN_PREPARACION (selección)");
            styleButton(btnPreparar, COLOR_PRINCIPAL_DARK);
            p.add(btnPreparar);

            btnPreparar.addActionListener(e -> {
                if (!puedeBodega()) { setMsg("Solo Admin/Bodega puede preparar."); return; }
                int row = table.getSelectedRow();
                if (row < 0) { setMsg("Selecciona un pedido en la tabla."); return; }
                int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
                boolean ok = sistema.prepararPedido(id);
                if (ok) { refrescarTabla(); setMsg("Pedido pasó a EN_PREPARACION: " + id); }
                else setMsg("No se pudo preparar (revisa estado).");
            });

            return p;
        }

        private void verDetalleSeleccion() {
            int row = table.getSelectedRow();
            if (row < 0) { setMsg("Selecciona un pedido en la tabla."); return; }

            int id = Integer.parseInt(String.valueOf(model.getValueAt(row, 0)));
            Pedido p = sistema.buscarPedido(id);
            if (p == null) { setMsg("Pedido no encontrado."); return; }

            Cliente c = p.getCliente();
            String nombre = (c != null) ? c.getNombre() : "N/A";
            String cedula = (c != null) ? c.getCedula() : "N/A";

            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(p.getId()).append("\n");
            sb.append("Cliente: ").append(nombre).append(" (").append(cedula).append(")\n");
            sb.append("Fecha: ").append(p.getFechaCreacion()).append("\n");
            sb.append("Estado: ").append(p.getEstado()).append("\n");
            sb.append("Total: $").append(p.getTotal()).append("\n\n");
            sb.append("--- Items ---\n");

            if (p.getDetalles().size() == 0) sb.append("Sin productos.\n");
            else {
                for (int i = 0; i < p.getDetalles().size(); i++) {
                    sb.append(i + 1).append(") ").append(p.getDetalles().get(i)).append("\n");
                }
            }

            areaDetalle.setText(sb.toString());
            setMsg("Detalle cargado para pedido #" + id);
        }
    }

    // =========================================================
    // REPORTES (mejorado y extendido)
    // =========================================================
    private class ReportesPanel extends JPanel {

        private final MainPanel parent;

        private JTextArea area;
        private JLabel lblMsg;

        public ReportesPanel(MainPanel parent) {
            this.parent = parent;

            setLayout(new BorderLayout(10, 10));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(COLOR_PRINCIPAL);

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
            top.setOpaque(false);

            JButton btnStockCritico = new JButton("Stock crítico");
            JButton btnPedidosEstado = new JButton("Pedidos por estado (todos)");
            JButton btnResumenVentas = new JButton("Resumen ventas");
            JButton btnProductoMasVendido = new JButton("Producto más vendido");
            JButton btnMayorComprador = new JButton("Mayor comprador");
            JButton btnComprasPorMes = new JButton("Compras por mes");
            JButton btnProductoPorStock = new JButton("Producto por stock");

            styleButton(btnStockCritico, COLOR_PRINCIPAL_DARK);
            styleButton(btnPedidosEstado, COLOR_ACCENT);
            styleButton(btnResumenVentas, COLOR_ACCENT);
            styleButton(btnProductoMasVendido, COLOR_PRINCIPAL_DARK);
            styleButton(btnMayorComprador, new Color(80, 160, 220));
            styleButton(btnComprasPorMes, new Color(0, 140, 100));
            styleButton(btnProductoPorStock, new Color(120, 120, 120));

            top.add(btnStockCritico);
            top.add(btnPedidosEstado);
            top.add(btnResumenVentas);
            top.add(btnProductoMasVendido);
            top.add(btnMayorComprador);
            top.add(btnComprasPorMes);
            top.add(btnProductoPorStock);

            area = new JTextArea();
            area.setFont(new Font("Consolas", Font.PLAIN, 12));
            area.setEditable(false);

            lblMsg = new JLabel(" ");
            lblMsg.setForeground(COLOR_ERROR);

            add(top, BorderLayout.NORTH);
            add(new JScrollPane(area), BorderLayout.CENTER);
            add(lblMsg, BorderLayout.SOUTH);

            // Mantener y enlazar todos los reportes (originales + nuevos)
            btnStockCritico.addActionListener(e -> reporteStockCritico());
            btnPedidosEstado.addActionListener(e -> reportePedidosPorEstadoTodos());
            btnResumenVentas.addActionListener(e -> reporteResumenVentas());
            btnProductoMasVendido.addActionListener(e -> reporteProductoMasVendido());
            btnMayorComprador.addActionListener(e -> reporteMayorComprador());
            btnComprasPorMes.addActionListener(e -> reporteComprasPorMes());
            btnProductoPorStock.addActionListener(e -> reporteProductoPorStock());
        }

        private void setMsg(String msg) {
            lblMsg.setText(msg);
            if (parent != null) parent.setStatus(msg);
        }

        // === Reportes existentes (mantener) ===
        private void reporteStockCritico() {
            ArrayList<Producto> list = sistema.getProductos();
            StringBuilder sb = new StringBuilder();
            sb.append("=== REPORTE STOCK CRÍTICO ===\n\n");

            boolean hay = false;
            for (Producto p : list) {
                if (p.estaBajoMinimo()) {
                    hay = true;
                    sb.append(String.format("%-10s | %-25s | Stock: %4d | Min: %4d | Tipo: %s\n", p.getCodigo(), p.getModelo(), p.getStock(), p.getStockMin(), p.getTipo()));
                }
            }
            if (!hay) sb.append("No hay productos críticos.\n");

            area.setText(sb.toString());
            setMsg("Reporte stock crítico generado.");
        }

        private void reportePedidosPorEstadoTodos() {
            String[] estados = {"PENDIENTE", "CONFIRMADO", "EN_PREPARACION", "ENVIADO"};

            ArrayList<Pedido> list = sistema.getPedidos();
            StringBuilder sb = new StringBuilder();
            sb.append("=== PEDIDOS POR ESTADO ===\n\n");

            for (String estStr : estados) {
                EstadoPedido est = EstadoPedido.valueOf(estStr);
                sb.append(">> ").append(est).append("\n");
                boolean hay = false;
                for (Pedido p : list) {
                    if (p.getEstado() == est) {
                        hay = true;
                        Cliente c = p.getCliente();
                        String nombre = (c != null) ? c.getNombre() : "N/A";
                        sb.append(String.format("ID:%4d | Cliente: %-20s | Cedula: %-10s | Fecha: %-10s | Total: %8.2f\n", p.getId(), nombre, (c!=null?c.getCedula():"N/A"), p.getFechaCreacion(), p.getTotal()));
                    }
                }
                if (!hay) sb.append("(sin pedidos)\n");
                sb.append("\n");
            }

            area.setText(sb.toString());
            setMsg("Reporte pedidos por estado generado.");
        }

        // === Reportes nuevos / modificados ===
        // Resumen ventas: total vendido (excluye PENDIENTE) y detalle por producto: codigo, nombre, precio, cantidad vendida
        private void reporteResumenVentas() {
            ArrayList<Pedido> pedidos = sistema.getPedidos();
            class PInfo { String modelo; double precio; int qty; double subtotal; }
            java.util.Map<String, PInfo> map = new java.util.HashMap<>();
            double total = 0.0;

            for (Pedido p : pedidos) {
                if (p.getEstado() == EstadoPedido.PENDIENTE) continue;
                for (DetallePedido d : p.getDetalles()) {
                    Producto prod = d.getProducto();
                    if (prod == null) continue;
                    String code = prod.getCodigo();
                    int q = d.getCantidad();
                    double price = prod.getPrecio();
                    PInfo info = map.get(code);
                    if (info == null) {
                        info = new PInfo();
                        info.modelo = prod.getModelo();
                        info.precio = price;
                        info.qty = q;
                        info.subtotal = price * q;
                        map.put(code, info);
                    } else {
                        info.qty += q;
                        info.subtotal += price * q;
                    }
                    total += price * q;
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== RESUMEN DE VENTAS ===\n");
            sb.append("Total vendido (sin pendientes): $").append(String.format("%.2f", total)).append("\n\n");
            sb.append(String.format("%-10s | %-25s | %-8s | %-8s | %-10s\n","COD","NOMBRE","PRECIO","CANT","SUBTOTAL"));
            sb.append("--------------------------------------------------------------------------------\n");
            for (java.util.Map.Entry<String, PInfo> e : map.entrySet()) {
                String cod = e.getKey();
                PInfo inf = e.getValue();
                sb.append(String.format("%-10s | %-25s | %8.2f | %8d | %10.2f\n", cod, inf.modelo, inf.precio, inf.qty, inf.subtotal));
            }

            area.setText(sb.toString());
            setMsg("Resumen de ventas generado.");
        }

        // Producto más vendido: muestra el producto con mayor cantidad vendida y la lista de compradores que lo adquirieron
        private void reporteProductoMasVendido() {
            ArrayList<Pedido> pedidos = sistema.getPedidos();
            java.util.Map<String, Integer> qtyByProduct = new java.util.HashMap<>();

            for (Pedido p : pedidos) {
                if (p.getEstado() == EstadoPedido.PENDIENTE) continue;
                for (DetallePedido d : p.getDetalles()) {
                    Producto prod = d.getProducto();
                    if (prod == null) continue;
                    qtyByProduct.put(prod.getCodigo(), qtyByProduct.getOrDefault(prod.getCodigo(), 0) + d.getCantidad());
                }
            }

            if (qtyByProduct.isEmpty()) {
                area.setText("No hay ventas para analizar.");
                setMsg("Reporte producto más vendido generado.");
                return;
            }

            String topCode = null;
            int max = -1;
            for (java.util.Map.Entry<String, Integer> e : qtyByProduct.entrySet()) {
                if (e.getValue() > max) { max = e.getValue(); topCode = e.getKey(); }
            }

            Producto topProd = null;
            for (Producto p : sistema.getProductos()) { if (p.getCodigo().equals(topCode)) { topProd = p; break; } }

            StringBuilder sb = new StringBuilder();
            sb.append("=== PRODUCTO MÁS VENDIDO ===\n\n");
            if (topProd != null) {
                sb.append("Código: ").append(topProd.getCodigo()).append("\n");
                sb.append("Nombre: ").append(topProd.getModelo()).append("\n");
                sb.append("Precio: $").append(String.format("%.2f", topProd.getPrecio())).append("\n");
                sb.append("Total unidades vendidas: ").append(max).append("\n\n");
            } else {
                sb.append("Código (sin objeto): ").append(topCode).append(" | Unidades: ").append(max).append("\n\n");
            }

            sb.append("--- Compradores de este producto ---\n");
            class CInfo { String nombre; int qty; double gasto; }
            java.util.Map<String, CInfo> buyers = new java.util.HashMap<>();

            for (Pedido ped : pedidos) {
                if (ped.getEstado() == EstadoPedido.PENDIENTE) continue;
                Cliente c = ped.getCliente();
                String ced = (c != null) ? c.getCedula() : "SIN_CLIENTE";
                String nombre = (c != null) ? c.getNombre() : "N/A";
                for (DetallePedido d : ped.getDetalles()) {
                    Producto prod = d.getProducto();
                    if (prod == null) continue;
                    if (!prod.getCodigo().equals(topCode)) continue;
                    CInfo ci = buyers.get(ced);
                    if (ci == null) { ci = new CInfo(); ci.nombre = nombre; ci.qty = d.getCantidad(); ci.gasto = d.getCantidad() * prod.getPrecio(); buyers.put(ced, ci); }
                    else { ci.qty += d.getCantidad(); ci.gasto += d.getCantidad() * prod.getPrecio(); }
                }
            }

            if (buyers.isEmpty()) sb.append("(nadie ha comprado este producto)\n");
            else {
                sb.append(String.format("%-12s | %-25s | %-6s | %-10s\n","CEDULA","NOMBRE","CANT","GASTO"));
                sb.append("---------------------------------------------------------------\n");
                for (java.util.Map.Entry<String, CInfo> be : buyers.entrySet()) {
                    String ced = be.getKey(); CInfo ci = be.getValue();
                    sb.append(String.format("%-12s | %-25s | %6d | %10.2f\n", ced, ci.nombre, ci.qty, ci.gasto));
                }
            }

            area.setText(sb.toString());
            setMsg("Reporte producto más vendido generado.");
        }

        // Mayor comprador: cliente que más gastó (y mostrar total de dinero y cantidad de productos comprados)
        private void reporteMayorComprador() {
            ArrayList<Pedido> pedidos = sistema.getPedidos();
            java.util.Map<String, Double> gastoPorCliente = new java.util.HashMap<>();
            java.util.Map<String, Integer> qtyPorCliente = new java.util.HashMap<>();
            java.util.Map<String, String> nombrePorCliente = new java.util.HashMap<>();

            for (Pedido p : pedidos) {
                if (p.getEstado() == EstadoPedido.PENDIENTE) continue;
                Cliente c = p.getCliente();
                String ced = (c != null) ? c.getCedula() : "SIN_CLIENTE";
                String nombre = (c != null) ? c.getNombre() : "N/A";
                nombrePorCliente.putIfAbsent(ced, nombre);
                for (DetallePedido d : p.getDetalles()) {
                    Producto prod = d.getProducto();
                    if (prod == null) continue;
                    double importe = prod.getPrecio() * d.getCantidad();
                    gastoPorCliente.put(ced, gastoPorCliente.getOrDefault(ced, 0.0) + importe);
                    qtyPorCliente.put(ced, qtyPorCliente.getOrDefault(ced, 0) + d.getCantidad());
                }
            }

            if (gastoPorCliente.isEmpty()) {
                area.setText("No hay compras registradas.");
                setMsg("Reporte mayor comprador generado.");
                return;
            }

            String topCed = null; double maxGasto = -1; int maxQty = 0;
            for (java.util.Map.Entry<String, Double> e : gastoPorCliente.entrySet()) {
                String ced = e.getKey(); double gasto = e.getValue();
                if (gasto > maxGasto) { maxGasto = gasto; topCed = ced; maxQty = qtyPorCliente.getOrDefault(ced, 0); }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== MAYOR COMPRADOR ===\n\n");
            sb.append("Cédula: ").append(topCed).append("\n");
            sb.append("Nombre: ").append(nombrePorCliente.getOrDefault(topCed, "N/A")).append("\n");
            sb.append("Total gastado: $").append(String.format("%.2f", maxGasto)).append("\n");
            sb.append("Total productos comprados: ").append(maxQty).append("\n\n");

            sb.append("--- Top compradores (por gasto) ---\n");
            sb.append(String.format("%-12s | %-25s | %-10s | %-6s\n","CEDULA","NOMBRE","GASTO","CANT"));
            sb.append("---------------------------------------------------------------\n");
            java.util.List<java.util.Map.Entry<String, Double>> list = new java.util.ArrayList<>(gastoPorCliente.entrySet());
            list.sort((a,b) -> Double.compare(b.getValue(), a.getValue()));
            for (java.util.Map.Entry<String, Double> e : list) {
                String ced = e.getKey(); double gasto = e.getValue(); int cant = qtyPorCliente.getOrDefault(ced, 0);
                sb.append(String.format("%-12s | %-25s | %10.2f | %6d\n", ced, nombrePorCliente.getOrDefault(ced, "N/A"), gasto, cant));
            }

            area.setText(sb.toString());
            setMsg("Reporte mayor comprador generado.");
        }

        // Compras por mes: mostrar pedidos por mes y sus items (ID, cliente, fecha, estado, total + detalle de items)
        private void reporteComprasPorMes() {
            ArrayList<Pedido> pedidos = sistema.getPedidos();
            java.util.Map<Integer, java.util.List<Pedido>> byMonth = new java.util.HashMap<>();
            for (int m = 1; m <= 12; m++) byMonth.put(m, new java.util.ArrayList<>());

            for (Pedido p : pedidos) {
                String fecha = p.getFechaCreacion();
                int month;
                try {
                    java.time.LocalDate ld = java.time.LocalDate.parse(fecha);
                    month = ld.getMonthValue();
                } catch (Exception ex) {
                    // si no se puede parsear, asignar 0 y omitir
                    continue;
                }
                java.util.List<Pedido> list = byMonth.get(month);
                if (list != null) list.add(p);
            }

            StringBuilder sb = new StringBuilder();
            sb.append("=== COMPRAS POR MES (LISTADO DE PEDIDOS) ===\n\n");
            for (int m = 1; m <= 12; m++) {
                java.time.Month mm = java.time.Month.of(m);
                sb.append("-- ").append(mm.name()).append(" (" + m + ") --\n");
                java.util.List<Pedido> list = byMonth.get(m);
                if (list == null || list.isEmpty()) {
                    sb.append("  (sin pedidos)\n\n");
                    continue;
                }

                for (Pedido ped : list) {
                    Cliente c = ped.getCliente();
                    String nombre = (c != null) ? c.getNombre() : "N/A";
                    String ced = (c != null) ? c.getCedula() : "N/A";
                    sb.append(String.format("Pedido ID:%4d | Cliente: %-20s | Cedula: %-12s | Fecha: %-10s | Estado: %-15s | Total: %8.2f\n",
                            ped.getId(), nombre, ced, ped.getFechaCreacion(), ped.getEstado(), ped.getTotal()));

                    // Detalle de items del pedido
                    if (ped.getDetalles() == null || ped.getDetalles().isEmpty()) {
                        sb.append("    (sin items)\n");
                    } else {
                        sb.append(String.format("    %-8s | %-30s | %8s | %6s | %10s\n", "COD", "NOMBRE", "PRECIO", "CANT", "SUBTOTAL"));
                        sb.append("    ---------------------------------------------------------------------------\n");
                        for (DetallePedido d : ped.getDetalles()) {
                            Producto prod = d.getProducto();
                            String cod = (prod != null) ? prod.getCodigo() : "N/A";
                            String nom = (prod != null) ? prod.getModelo() : "N/A";
                            double precio = (prod != null) ? prod.getPrecio() : 0.0;
                            int cant = d.getCantidad();
                            double sub = precio * cant;
                            sb.append(String.format("    %-8s | %-30s | %8.2f | %6d | %10.2f\n", cod, nom, precio, cant, sub));
                        }
                    }
                    sb.append("\n");
                }
                sb.append("\n");
            }

            area.setText(sb.toString());
            setMsg("Reporte compras por mes (pedidos) generado.");
        }

        // Productos por stock: lista productos con stock actual y stockMin
        private void reporteProductoPorStock() {
            ArrayList<Producto> list = sistema.getProductos();
            StringBuilder sb = new StringBuilder();
            sb.append("=== PRODUCTOS - STOCK ACTUAL ===\n\n");
            sb.append(String.format("%-10s | %-30s | %-6s | %-6s | %-12s\n","COD","NOMBRE","STOCK","MIN","TIPO"));
            sb.append("---------------------------------------------------------------------\n");
            for (Producto p : list) {
                String cod = p.getCodigo() != null ? p.getCodigo() : "N/A";
                String nom = p.getModelo() != null ? p.getModelo() : "N/A";
                int stock = p.getStock();
                int min = p.getStockMin();
                String tipo = p.getTipo() != null ? p.getTipo() : "N/A";
                sb.append(String.format("%-10s | %-30s | %6d | %6d | %-12s\n", cod, nom, stock, min, tipo));
            }

            area.setText(sb.toString());
            setMsg("Reporte producto por stock generado.");
        }
    }
}
