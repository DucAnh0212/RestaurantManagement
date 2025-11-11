package com.nhom.restaurant.gui;

import com.nhom.restaurant.models.MenuItems;
import com.nhom.restaurant.utils.ImageCache;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MenuPanel extends JPanel {

    private JPanel categoryButtonPanel;
    private JButton selectedCategoryButton;
    private Font categoryFontInactive = new Font("Arial", Font.BOLD, 18);
    private Font categoryFontActive = new Font("Arial", Font.BOLD | Font.ITALIC, 18);
    private JPanel menuItemsGridPanel;
    private NumberFormat currencyFormatter;
    private final Pattern DIACRITICS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    public MenuPanel() {
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(createCategoryPanel(), BorderLayout.WEST);
        add(createMenuGridPanel(), BorderLayout.CENTER);
        try { loadCategories(); } catch (SQLException e) { e.printStackTrace(); add(new JLabel("Lỗi: Không thể tải Menu.")); }
    }

    private JComponent createCategoryPanel() {
        categoryButtonPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        categoryButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 5));
        JScrollPane scrollPane = new JScrollPane(categoryButtonPanel);
        scrollPane.setPreferredSize(new Dimension(200, 0));
        scrollPane.setBorder(null);
        return scrollPane;
    }

    private JComponent createMenuGridPanel() {
        menuItemsGridPanel = new JPanel(new GridLayout(0, 5, 15, 15));
        menuItemsGridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(menuItemsGridPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách món ăn"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private String normalizeCategory(String category) {
        if (category == null) return "Không xác định";
        if (category.equalsIgnoreCase("Đồ UỐng") || category.equalsIgnoreCase("Đồ Uống")) return "Đồ Uống";
        return category;
    }

    private void loadCategories() throws SQLException {
        categoryButtonPanel.removeAll(); selectedCategoryButton = null;
        JButton allButton = createCategoryButton("Tất cả");
        List<MenuItems> allItems = MenuItems.findAll();
        List<String> categories = allItems.stream().map(MenuItems::getCategory).map(this::normalizeCategory).distinct().sorted().collect(Collectors.toList());
        for (String category : categories) createCategoryButton(category);
        allButton.doClick(); categoryButtonPanel.revalidate(); categoryButtonPanel.repaint();
    }

    private JButton createCategoryButton(String categoryName) {
        JButton button = new JButton(categoryName);
        button.setFont(categoryFontInactive); button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); button.setPreferredSize(new Dimension(0, 50));
        button.setForeground(Color.BLACK); button.setOpaque(false);
        button.setContentAreaFilled(false); button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        button.addActionListener(e -> {
            try {
                if (selectedCategoryButton != null) {
                    selectedCategoryButton.setFont(categoryFontInactive); selectedCategoryButton.setForeground(Color.BLACK);
                }
                button.setFont(categoryFontActive); button.setForeground(Color.BLACK);
                selectedCategoryButton = button; loadMenuItems(categoryName);
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        categoryButtonPanel.add(button); return button;
    }

    private String normalizeName(String s) {
        if (s == null) return "";
        String nfdNormalizedString = Normalizer.normalize(s, Normalizer.Form.NFD);
        String noAccents = DIACRITICS_PATTERN.matcher(nfdNormalizedString).replaceAll("");
        noAccents = noAccents.replaceAll("Đ", "D").replaceAll("đ", "d");
        String lower = noAccents.toLowerCase();
        String withUnderscores = lower.replaceAll("\\s+", "_");
        String clean = withUnderscores.replaceAll("[^a-z0-9_]", "");
        return clean;
    }

    private String getImagePathForItem(MenuItems item) {
        if (item == null || item.getName() == null) return null;
        String normalizedName = normalizeName(item.getName());
        String basePath = "images/" + normalizedName;
        File pngFile = new File(basePath + ".png");
        if (pngFile.exists()) return basePath + ".png";
        File jpgFile = new File(basePath + ".jpg");
        if (jpgFile.exists()) return basePath + ".jpg";
        File jpegFile = new File(basePath + ".jpeg");
        if (jpegFile.exists()) return basePath + ".jpeg";
        return null;
    }

    private void loadMenuItems(String category) throws SQLException {
        menuItemsGridPanel.removeAll();
        List<MenuItems> itemsToDisplay;
        if (category.equals("Tất cả")) itemsToDisplay = MenuItems.findAll();
        else if (category.equals("Đồ Uống")) {
            itemsToDisplay = MenuItems.findAllByCategory("Đồ Uống"); itemsToDisplay.addAll(MenuItems.findAllByCategory("Đồ UỐng"));
        } else itemsToDisplay = MenuItems.findAllByCategory(category);
        for (MenuItems item : itemsToDisplay) menuItemsGridPanel.add(createMenuItemCard(item));
        menuItemsGridPanel.revalidate(); menuItemsGridPanel.repaint();
    }

    private JPanel createMenuItemCard(MenuItems item) {
        JPanel card = new JPanel(new BorderLayout(0, 0));

        card.setPreferredSize(new Dimension(220, 230));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        JLabel imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(200, 130));

        String imagePath = getImagePathForItem(item);
        if (imagePath != null) {

            ImageIcon icon = ImageCache.getImage(imagePath, 200, 130);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setText("<html><center><i>Lỗi ảnh</i></center></html>");
            }
        } else {
            imageLabel.setText("<html><center><i>Chưa có<br>hình ảnh</i></center></html>");
            imageLabel.setForeground(Color.GRAY);
        }
        card.add(imageLabel, BorderLayout.NORTH);

        String priceFormatted = currencyFormatter.format(item.getPrice());
        String htmlText = String.format("<html><center><font size='5'><b>%s</b></font><br><font size='4' color='red'>%s</font></center></html>",
                item.getName(), priceFormatted);

        JLabel nameLabel = new JLabel(htmlText);
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setVerticalAlignment(SwingConstants.TOP);
        card.add(nameLabel, BorderLayout.CENTER);

        return card;
    }
}