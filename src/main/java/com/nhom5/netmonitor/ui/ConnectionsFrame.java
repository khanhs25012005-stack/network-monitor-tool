
package com.nhom5.netmonitor.ui;

import com.nhom5.netmonitor.core.ConnectionInfo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Dimension;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;

/**
 * Connections page.
 * Search, filter and display network connections.
 */
public final class ConnectionsFrame extends MonitorFrame {

    private final JTable table;
    private final ConnectionTableModel tableModel;
    private final TableRowSorter<ConnectionTableModel> sorter;

    private final JTextField search = new JTextField();

    private String protocolFilter = "All";
    private String stateFilter = "Tất cả trạng thái";

    private int totalConnectionCount = 248;

    private final JLabel countLabel = UiTheme.label(
            "Hiển thị 15 / 248 kết nối",
            0, 0, 180, 36,
            12, UiTheme.MUTED, false
    );

    private final JLabel pageLabel = UiTheme.label(
            "Trang 1 / 17",
            18, 652, 150, 34,
            12, UiTheme.MUTED, false
    );

    public ConnectionsFrame() {

        super("Connections", 1, 1440, 900);

        tableModel = new ConnectionTableModel(sampleRows());

        table = new JTable(tableModel);

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        addToolbar();
        styleTable();
        addTableCard();

        applyFilters();
    }

    // =====================================================
    // TABLE CARD
    // =====================================================

    private void addTableCard() {

        SurfacePanel tableCard = new SurfacePanel(
                264, 176, 1152, 700
        );

        JScrollPane scroll = new JScrollPane(table);

        scroll.setBounds(1, 1, 1150, 644);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        scroll.getViewport().setBackground(UiTheme.CARD);
        scroll.setBackground(UiTheme.CARD);

        scroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );

        tableCard.add(scroll);

        tableCard.add(pageLabel);

        addPager(tableCard);

        addPageComponent(tableCard);
    }

    // =====================================================
    // UPDATE CONNECTION DATA
    // =====================================================

    public void setConnections(List<ConnectionInfo> connections) {

        if (connections == null) {
            return;
        }

        List<ConnectionInfo> snapshot = List.copyOf(connections);

        Runnable update = () -> {

            totalConnectionCount = snapshot.size();

            tableModel.setRows(snapshot);

            applyFilters();
        };

        if (SwingUtilities.isEventDispatchThread()) {
            update.run();
        } else {
            SwingUtilities.invokeLater(update);
        }
    }

    // =====================================================
    // TOOLBAR
    // =====================================================

    private void addToolbar() {

        SurfacePanel bar = new SurfacePanel(
                264, 88, 1152, 72
        );

        // SEARCH FIELD

        search.setText("");

        search.putClientProperty(
                "JTextField.placeholderText",
                "Tìm theo địa chỉ IP..."
        );

        search.setBounds(18, 18, 320, 36);

        styleField(search);

        search.getDocument().addDocumentListener(
                new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                applyFilters();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                applyFilters();
            }
        });

        bar.add(search);

        // PROTOCOL FILTER

        String[] protocols = {
            "All",
            "TCP",
            "UDP"
        };

        int x = 354;

        for (String value : protocols) {

            String displayText = value.equals("All")
                    ? "Tất cả"
                    : value;

            int width = value.equals("All") ? 92 : 76;

            JButton button = button(
                    displayText,
                    x, 18, width, 36,
                    value.equals(protocolFilter)
            );

            button.addActionListener(e -> {

                protocolFilter = value;

                applyFilters();

                updateProtocolButtons(bar);
            });

            bar.add(button);

            x += width;
        }

        // STATE FILTER

        JComboBox<String> states = new JComboBox<>(
                new String[]{
                    "Tất cả trạng thái",
                    "ESTABLISHED",
                    "LISTEN",
                    "TIME_WAIT",
                    "CLOSE_WAIT",
                    "SYN_SENT"
                }
        );

        states.setBounds(604, 18, 200, 36);

        states.setBackground(UiTheme.INPUT);
        states.setForeground(UiTheme.WHITE);

        states.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                12
        ));

        states.setFocusable(false);

        states.addActionListener(e -> {

            stateFilter = (String) states.getSelectedItem();

            applyFilters();
        });

        bar.add(states);

        // CONNECTION COUNT

        countLabel.setBounds(820, 18, 180, 36);

        bar.add(countLabel);

        // REFRESH BUTTON

        JButton refresh = button(
                "Làm mới",
                1012, 18, 122, 36,
                false
        );

        refresh.addActionListener(e -> {

            search.setText("");

            states.setSelectedIndex(0);

            protocolFilter = "All";
            stateFilter = "Tất cả trạng thái";

            totalConnectionCount = 248;

            tableModel.setRows(sampleRows());

            applyFilters();

            updateProtocolButtons(bar);
        });

        bar.add(refresh);

        addPageComponent(bar);
    }

    // =====================================================
    // UPDATE PROTOCOL BUTTONS
    // =====================================================

    private void updateProtocolButtons(SurfacePanel bar) {

        for (Component component : bar.getComponents()) {

            if (component instanceof JButton b) {

                String text = b.getText();

                if (text.equals("Tất cả")
                        || text.equals("TCP")
                        || text.equals("UDP")) {

                    boolean selected =
                            (protocolFilter.equals("All")
                            && text.equals("Tất cả"))
                            || protocolFilter.equals(text);

                    b.setBackground(
                            selected ? UiTheme.BLUE : UiTheme.INPUT
                    );
                }
            }
        }

        bar.repaint();
    }

    // =====================================================
    // FILTER DATA
    // =====================================================

    private void applyFilters() {

        String text = search.getText()
                .trim()
                .toLowerCase();

        sorter.setRowFilter(new RowFilter<>() {

            @Override
            public boolean include(
                    Entry<? extends ConnectionTableModel,
                    ? extends Integer> entry
            ) {

                String protocol = entry.getStringValue(0);
                String state = entry.getStringValue(5);

                boolean matchesProtocol =
                        protocolFilter.equals("All")
                        || protocolFilter.equals(protocol);

                boolean matchesState =
                        stateFilter.equals("Tất cả trạng thái")
                        || stateFilter.equals(state);

                boolean matchesText = text.isEmpty();

                // Search across all table columns.
                for (int i = 0; !matchesText && i < 7; i++) {

                    String value = entry.getStringValue(i);

                    if (value != null) {
                        matchesText = value
                                .toLowerCase()
                                .contains(text);
                    }
                }

                return matchesProtocol
                        && matchesState
                        && matchesText;
            }
        });

        countLabel.setText(
                "Hiển thị "
                + table.getRowCount()
                + " / "
                + totalConnectionCount
                + " kết nối"
        );

        updatePageLabel();
    }

    private void updatePageLabel() {

        pageLabel.setText("Trang 1 / 17");
    }

    // =====================================================
    // TABLE STYLE
    // =====================================================

    private void styleTable() {

        table.setRowHeight(44);

        table.setFont(new Font(
                "Consolas",
                Font.PLAIN,
                13
        ));

        table.setForeground(UiTheme.WHITE);
        table.setBackground(UiTheme.CARD);
        table.setGridColor(UiTheme.EDGE);

        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        table.setSelectionBackground(
                new Color(34, 53, 82)
        );

        table.setSelectionForeground(UiTheme.WHITE);

        table.setFillsViewportHeight(true);

        table.setIntercellSpacing(
                new Dimension(0, 1)
        );

        // TABLE HEADER

        table.getTableHeader().setPreferredSize(
                new Dimension(1, 48)
        );

        table.getTableHeader().setBackground(
                UiTheme.SIDEBAR
        );

        table.getTableHeader().setForeground(
                UiTheme.MUTED
        );

        table.getTableHeader().setFont(new Font(
                "Segoe UI",
                Font.BOLD,
                11
        ));

        table.getTableHeader().setReorderingAllowed(false);

        // CELL RENDERER

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable t,
                    Object value,
                    boolean selected,
                    boolean focus,
                    int row,
                    int column
            ) {

                JLabel cell = (JLabel)
                        super.getTableCellRendererComponent(
                                t,
                                value,
                                selected,
                                focus,
                                row,
                                column
                        );

                cell.setBorder(
                        BorderFactory.createEmptyBorder(
                                0, 12, 0, 8
                        )
                );

                cell.setBackground(
                        selected
                                ? new Color(34, 53, 82)
                                : (row % 2 == 0
                                        ? UiTheme.CARD
                                        : new Color(22, 35, 56))
                );

                cell.setForeground(UiTheme.WHITE);

                // ALIGNMENT

                if (column == 2
                        || column == 4
                        || column == 6) {

                    cell.setHorizontalAlignment(
                            JLabel.RIGHT
                    );

                } else {

                    cell.setHorizontalAlignment(
                            JLabel.LEFT
                    );
                }

                // PROTOCOL COLOR

                if (column == 0) {

                    cell.setFont(new Font(
                            "Segoe UI",
                            Font.BOLD,
                            12
                    ));

                    cell.setForeground(
                            "UDP".equals(value)
                                    ? UiTheme.TEAL
                                    : UiTheme.UPLOAD
                    );
                }

                // CONNECTION STATE COLOR

                if (column == 5) {

                    cell.setFont(new Font(
                            "Consolas",
                            Font.BOLD,
                            11
                    ));

                    String state = String.valueOf(value);

                    if (state.equals("ESTABLISHED")) {

                        cell.setForeground(UiTheme.GREEN);

                    } else if (state.equals("—")) {

                        cell.setForeground(UiTheme.MUTED);

                    } else {

                        cell.setForeground(UiTheme.AMBER);
                    }
                }

                return cell;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {

            table.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);
        }

        // COLUMN WIDTHS

        int[] widths = {
            105,
            220,
            130,
            220,
            130,
            210,
            100
        };

        for (int i = 0; i < widths.length; i++) {

            table.getColumnModel()
                    .getColumn(i)
                    .setPreferredWidth(widths[i]);
        }

        // SELECT SAMPLE ROW

        if (table.getRowCount() > 4) {

            table.setRowSelectionInterval(4, 4);
        }
    }

    // =====================================================
    // PAGINATION
    // =====================================================

    private static void addPager(SurfacePanel card) {

        String[] labels = {
            "‹",
            "1",
            "2",
            "3",
            "…",
            "17",
            "›"
        };

        /*
         * Tổng chiều rộng:
         * - Các nút phân trang: 298 px
         * - Khoảng cách: 12 px
         * - ComboBox: 144 px
         *
         * Tất cả được căn phải trong card.
         */

        int startX = 680;
        int y = 652;
        int gap = 6;

        for (String text : labels) {

            int width =
                    (text.equals("‹") || text.equals("›"))
                    ? 36
                    : 38;

            JButton b = button(
                    text,
                    startX,
                    y,
                    width,
                    34,
                    text.equals("1")
            );

            b.setFont(new Font(
                    "Segoe UI",
                    Font.PLAIN,
                    12
            ));

            card.add(b);

            startX += width + gap;
        }

        // PAGE SIZE COMBOBOX

        JComboBox<String> pageSize = new JComboBox<>(
                new String[]{
                    "15 dòng / trang",
                    "30 dòng / trang",
                    "50 dòng / trang"
                }
        );

        pageSize.setBounds(
                990, y, 144, 34
        );

        pageSize.setBackground(UiTheme.INPUT);
        pageSize.setForeground(UiTheme.WHITE);

        pageSize.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                12
        ));

        pageSize.setFocusable(false);

        card.add(pageSize);
    }

    // =====================================================
    // BUTTON STYLE
    // =====================================================

    private static JButton button(
            String text,
            int x,
            int y,
            int w,
            int h,
            boolean selected
    ) {

        JButton b = new JButton(text);

        b.setBounds(x, y, w, h);

        b.setFocusPainted(false);
        b.setFocusable(false);

        b.setFont(new Font(
                "Segoe UI",
                Font.PLAIN,
                12
        ));

        b.setForeground(UiTheme.WHITE);

        b.setBackground(
                selected ? UiTheme.BLUE : UiTheme.INPUT
        );

        b.setBorder(
                BorderFactory.createLineBorder(UiTheme.EDGE)
        );

        return b;
    }

    // =====================================================
    // SEARCH FIELD STYLE
    // =====================================================

    private static void styleField(JTextField field) {

        field.setBackground(UiTheme.INPUT);
        field.setForeground(UiTheme.WHITE);
        field.setCaretColor(UiTheme.WHITE);

        field.setFont(new Font(
                "Consolas",
                Font.PLAIN,
                13
        ));

        field.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(UiTheme.EDGE),
                        BorderFactory.createEmptyBorder(
                                0, 12, 0, 8
                        )
                )
        );
    }

    // =====================================================
    // SAMPLE DATA
    // =====================================================

    private static List<ConnectionInfo> sampleRows() {

        return List.of(

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52144,
                    "142.250.199.78", 443,
                    "ESTABLISHED", 8124
            ),

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52160,
                    "20.190.151.9", 443,
                    "ESTABLISHED", 8124
            ),

            new ConnectionInfo(
                    "TCP", "0.0.0.0", 135,
                    "0.0.0.0", 0,
                    "LISTEN", 1032
            ),

            new ConnectionInfo(
                    "TCP", "0.0.0.0", 445,
                    "0.0.0.0", 0,
                    "LISTEN", 4
            ),

            new ConnectionInfo(
                    "TCP", "127.0.0.1", 49711,
                    "127.0.0.1", 3306,
                    "ESTABLISHED", 5620
            ),

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52201,
                    "151.101.1.69", 443,
                    "TIME_WAIT", 0
            ),

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52233,
                    "13.107.42.14", 443,
                    "CLOSE_WAIT", 9340
            ),

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52290,
                    "172.217.24.14", 80,
                    "SYN_SENT", 8124
            ),

            new ConnectionInfo(
                    "UDP", "0.0.0.0", 5353,
                    "*", 0,
                    "—", 2216
            ),

            new ConnectionInfo(
                    "UDP", "192.168.1.25", 137,
                    "*", 0,
                    "—", 4
            ),

            new ConnectionInfo(
                    "UDP", "127.0.0.1", 1900,
                    "*", 0,
                    "—", 7788
            ),

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52318,
                    "104.18.32.7", 443,
                    "ESTABLISHED", 11208
            ),

            new ConnectionInfo(
                    "TCP", "0.0.0.0", 3389,
                    "0.0.0.0", 0,
                    "LISTEN", 1180
            ),

            new ConnectionInfo(
                    "UDP", "192.168.1.25", 138,
                    "*", 0,
                    "—", 4
            ),

            new ConnectionInfo(
                    "TCP", "192.168.1.25", 52344,
                    "52.96.165.2", 443,
                    "TIME_WAIT", 0
            )
        );
    }

    // =====================================================
    // MAIN
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new ConnectionsFrame().setVisible(true);
        });
    }
}
