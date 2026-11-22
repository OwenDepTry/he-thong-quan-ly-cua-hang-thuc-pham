-- Ảnh sản phẩm (chạy sau dữ liệu mẫu; chạy lại nhiều lần không sao).
--  * DuLieu: ảnh quản lý tải lên trên web, lưu thẳng trong database (bền khi deploy gói Free, không cần ổ đĩa).
--  * Url:    ảnh ở nơi khác. Ảnh mẫu dưới đây là ảnh thật trên Wikimedia Commons, giấy phép tự do,
--            ghi nguồn ở NguonAnh và TrangNguon (trang có đầy đủ tác giả, giấy phép).
-- Xóa sản phẩm thì ảnh xóa theo (ON DELETE CASCADE), app desktop không cần biết bảng này.

CREATE TABLE IF NOT EXISTS SanPhamHinhAnh (
    MaSanPham   VARCHAR(50) PRIMARY KEY REFERENCES SanPham(MaSanPham) ON DELETE CASCADE,
    DuLieu      BYTEA,
    LoaiNoiDung VARCHAR(50),
    Url         VARCHAR(1000),
    NguonAnh    VARCHAR(300),
    TrangNguon  VARCHAR(1000),
    CapNhat     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT sanphamhinhanh_co_anh CHECK (DuLieu IS NOT NULL OR Url IS NOT NULL)
);

INSERT INTO SanPhamHinhAnh (MaSanPham, Url, NguonAnh, TrangNguon)
SELECT v.ma, v.url, v.nguon, v.trang
FROM (VALUES
    ('SP001', 'https://commons.wikimedia.org/wiki/Special:FilePath/Rib-steak-raw-MCB.jpg?width=500', 'Michael C. Berch, CC BY-SA 2.5, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Rib-steak-raw-MCB.jpg'),
    ('SP002', 'https://commons.wikimedia.org/wiki/Special:FilePath/Raw_Meat_(6737753521).jpg?width=500', 'Jen Arrr, CC BY-SA 2.0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Raw_Meat_(6737753521).jpg'),
    ('SP003', 'https://commons.wikimedia.org/wiki/Special:FilePath/Raw_salmon_fillets.jpg?width=500', 'FULVIO_TOGNON, CC0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Raw_salmon_fillets.jpg'),
    ('SP004', 'https://commons.wikimedia.org/wiki/Special:FilePath/Shrimp.jpg?width=500', 'pinay06, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Shrimp.jpg'),
    ('SP005', 'https://commons.wikimedia.org/wiki/Special:FilePath/Carrots.JPG?width=500', 'Kander, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Carrots.JPG'),
    ('SP006', 'https://commons.wikimedia.org/wiki/Special:FilePath/Ipomoea_aquatica.jpg?width=500', 'Eric in SF, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Ipomoea_aquatica.jpg'),
    ('SP007', 'https://commons.wikimedia.org/wiki/Special:FilePath/Chinese_vegetable_027.jpg?width=500', 'Anna Frodesiak, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Chinese_vegetable_027.jpg'),
    ('SP008', 'https://commons.wikimedia.org/wiki/Special:FilePath/Shiny_red_apples.jpg?width=500', 'Leon Brooks, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Shiny_red_apples.jpg'),
    ('SP009', 'https://commons.wikimedia.org/wiki/Special:FilePath/Bowl_of_oranges.jpg?width=500', 'U.S. Department of Agriculture, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Bowl_of_oranges.jpg'),
    ('SP010', 'https://commons.wikimedia.org/wiki/Special:FilePath/Green_grape_fruit.jpg?width=500', 'Paolo Neo, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Green_grape_fruit.jpg'),
    ('SP011', 'https://commons.wikimedia.org/wiki/Special:FilePath/Milk-bottle.jpg?width=500', 'FiveRings, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Milk-bottle.jpg'),
    ('SP012', 'https://commons.wikimedia.org/wiki/Special:FilePath/Plain_Yogurt.jpg?width=500', 'Alabama Extension, CC0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Plain_Yogurt.jpg'),
    ('SP013', 'https://commons.wikimedia.org/wiki/Special:FilePath/Aquafina_-_Bottled_Water_(47109162561).jpg?width=500', 'Tony Webster, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Aquafina_-_Bottled_Water_(47109162561).jpg'),
    ('SP014', 'https://commons.wikimedia.org/wiki/Special:FilePath/Instant_coffee.jpg?width=500', 'Editor at Large, CC BY-SA 2.5, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Instant_coffee.jpg'),
    ('SP015', 'https://commons.wikimedia.org/wiki/Special:FilePath/Thai_jasmine_rice_uncooked.jpg?width=500', 'Takeaway, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Thai_jasmine_rice_uncooked.jpg'),
    ('SP016', 'https://commons.wikimedia.org/wiki/Special:FilePath/N%C6%B0%E1%BB%9Bc_m%E1%BA%AFm_bottle.JPG?width=500', 'Bdanh, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:N%C6%B0%E1%BB%9Bc_m%E1%BA%AFm_bottle.JPG'),
    ('SP017', 'https://commons.wikimedia.org/wiki/Special:FilePath/CanolaOil_bottle.jpg?width=500', 'Whitebox, GFDL, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:CanolaOil_bottle.jpg'),
    ('SP018', 'https://commons.wikimedia.org/wiki/Special:FilePath/InstantNoodles.JPG?width=500', 'Kcdtsg, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:InstantNoodles.JPG'),
    ('SP019', 'https://commons.wikimedia.org/wiki/Special:FilePath/Bowl_of_white_sugar.jpg?width=500', 'Douglas P Perkins, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Bowl_of_white_sugar.jpg'),
    ('SP020', 'https://commons.wikimedia.org/wiki/Special:FilePath/Slices_of_bread.jpg?width=500', 'National Cancer Institute, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Slices_of_bread.jpg'),
    ('SP021', 'https://commons.wikimedia.org/wiki/Special:FilePath/Danish-butter-cookies-1032894.jpg?width=500', 'sdnet01, CC0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Danish-butter-cookies-1032894.jpg'),
    ('SP022', 'https://commons.wikimedia.org/wiki/Special:FilePath/6-Pack-Chicken-Eggs.jpg?width=500', 'Evan-Amos, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:6-Pack-Chicken-Eggs.jpg'),
    ('SP023', 'https://commons.wikimedia.org/wiki/Special:FilePath/Japanese_tofu_001.jpg?width=500', 'Ocdp, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Japanese_tofu_001.jpg'),
    ('SP024', 'https://commons.wikimedia.org/wiki/Special:FilePath/Potatoes.jpg?width=500', 'U.S. Department of Agriculture, Public domain, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Potatoes.jpg'),
    ('SP025', 'https://commons.wikimedia.org/wiki/Special:FilePath/Ripe_tomatoes.JPG?width=500', 'Amtsga, CC0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Ripe_tomatoes.JPG'),
    ('SP026', 'https://commons.wikimedia.org/wiki/Special:FilePath/Ripening_bananas_(Unsplash).jpg?width=500', 'Scott Webb, CC0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Ripening_bananas_(Unsplash).jpg'),
    ('SP027', 'https://commons.wikimedia.org/wiki/Special:FilePath/Ripe_mangoes.jpg?width=500', 'Temmydolph, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Ripe_mangoes.jpg'),
    ('SP028', 'https://commons.wikimedia.org/wiki/Special:FilePath/Kycklingfil%C3%A9.jpg?width=500', 'Dnor, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Kycklingfil%C3%A9.jpg'),
    ('SP029', 'https://commons.wikimedia.org/wiki/Special:FilePath/Frozen_basa_fish_fillet.jpg?width=500', 'Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Frozen_basa_fish_fillet.jpg'),
    ('SP030', 'https://commons.wikimedia.org/wiki/Special:FilePath/Fresh_Seafood.jpg?width=500', 'Petr Kratochvil, CC0, Wikimedia Commons', 'https://commons.wikimedia.org/wiki/File:Fresh_Seafood.jpg')
) AS v(ma, url, nguon, trang)
WHERE EXISTS (SELECT 1 FROM SanPham s WHERE s.MaSanPham = v.ma)
ON CONFLICT (MaSanPham) DO NOTHING;
