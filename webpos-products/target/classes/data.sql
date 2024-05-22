-- CREATE TABLE IF NOT EXISTS product (
--     id VARCHAR(255) PRIMARY KEY,
--     _id VARCHAR(255) NOT NULL,
--     price VARCHAR(255) NOT NULL,
--     category VARCHAR(255) NOT NULL,
--     quantity INT NOT NULL,
--     name VARCHAR(255) NOT NULL,
--     stock INT NOT NULL,
--     img VARCHAR(255)
-- );

-- INSERT INTO product VALUES ('1', '1', '3', 'drink', 16, 'cola', 1, 'Cola.jpg'),
--        ('2', '2', '4', 'drink', 12, 'sprite', 1, 'Sprite.png'),
--        ('3', '3', '5', 'drink', 5, 'red bull', 1, 'Redbull.jpg'),
--        ('4', '4', '5', 'drink', 5, 'Milk', 1, 'Milk.jpg');


-- 嵌套设置信息表格
CREATE TABLE IF NOT EXISTS nested_settings (
    id BIGINT PRIMARY KEY,
    app VARCHAR(255) NOT NULL,
    store VARCHAR(255) NOT NULL,
    address_one VARCHAR(255) NOT NULL,
    address_two VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    tax VARCHAR(255) NOT NULL,
    symbol VARCHAR(255) NOT NULL,
    percentage VARCHAR(255) NOT NULL,
    footer VARCHAR(255) NOT NULL,
    img VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS settings (
    _id BIGINT PRIMARY KEY,
    id VARCHAR(255) NOT NULL,
    nested_settings_id BIGINT NOT NULL, -- 外键，关联嵌套设置信息表格
    FOREIGN KEY (nested_settings_id) REFERENCES nested_settings(id)
);

-- 初始化数据
INSERT INTO nested_settings
VALUES (1, 'Standalone Point of Sale', 'Store-Pos', '10086', '10087', '15968774896', '', '$', '10', '', '');

INSERT INTO settings VALUES (1, 'd36d', 1);

CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(255) PRIMARY KEY,
    _id INT NOT NULL,
    name VARCHAR(255)
);

INSERT INTO categories VALUES ('1711853606', 1711853606, 'drink');