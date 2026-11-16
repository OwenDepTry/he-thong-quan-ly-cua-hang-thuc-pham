package ui;

import java.awt.FlowLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * Thanh công cụ CRUD: nút nhỏ gọn có icon. Chỉ "Thêm" dùng màu chính, "Xóa" màu đỏ,
 * các nút còn lại trung tính để màn hình không bị rối mắt.
 */
public class CrudToolbarPanel extends JPanel {

    private final Map<String, JButton> buttonMap = new LinkedHashMap<>();

    public CrudToolbarPanel(String... buttonNames) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 0));
        setOpaque(false);

        for (String name : buttonNames) {
            JButton btn = createButton(name);
            buttonMap.put(name, btn);
            add(btn);
        }
    }

    private JButton createButton(String text) {
        AppButton.Variant variant;
        switch (text) {
            case "Thêm":
                variant = AppButton.Variant.PRIMARY;
                break;
            case "Xóa":
                variant = AppButton.Variant.DANGER;
                break;
            default:
                variant = AppButton.Variant.SECONDARY;
                break;
        }
        AppButton btn = new AppButton(resolveDisplayText(text), AppIcons.nameFor(text), variant);
        btn.setToolTipText(resolveTooltip(text));
        return btn;
    }

    private String resolveDisplayText(String text) {
        switch (text) {
            case "Xuất excel": return "Xuất CSV";
            case "Nhập excel": return "Nhập CSV";
            default: return text;
        }
    }

    private String resolveTooltip(String text) {
        switch (text) {
            case "Thêm": return "Thêm bản ghi mới";
            case "Xóa": return "Xóa dòng đang chọn";
            case "Sửa": return "Sửa dòng đang chọn";
            case "Chi tiết": return "Xem chi tiết dòng đang chọn";
            case "In PDF": return "In dòng đang chọn ra file PDF";
            case "Xuất excel": return "Xuất bảng ra file CSV (mở được bằng Excel)";
            case "Nhập excel": return "Nhập dữ liệu từ file CSV";
            default: return text;
        }
    }

    public JButton getButton(String name) {
        return buttonMap.get(name);
    }
}
