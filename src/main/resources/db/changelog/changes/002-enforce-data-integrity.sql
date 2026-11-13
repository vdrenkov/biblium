ALTER TABLE authors
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN surname SET NOT NULL;

ALTER TABLE books
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN publish_date SET NOT NULL,
    ALTER COLUMN quantity SET NOT NULL,
    ALTER COLUMN author_id SET NOT NULL,
    ADD CONSTRAINT ck_books_quantity CHECK (quantity >= 0);

ALTER TABLE clients
    ALTER COLUMN name SET NOT NULL,
    ALTER COLUMN surname SET NOT NULL,
    ALTER COLUMN email SET NOT NULL,
    ALTER COLUMN phone_number SET NOT NULL;

ALTER TABLE orders
    ALTER COLUMN issue_date SET NOT NULL,
    ALTER COLUMN due_date SET NOT NULL,
    ALTER COLUMN client_id SET NOT NULL,
    ADD CONSTRAINT ck_orders_dates CHECK (due_date >= issue_date);

ALTER TABLE users
    ALTER COLUMN username SET NOT NULL,
    ALTER COLUMN password SET NOT NULL;

ALTER TABLE user_roles ALTER COLUMN role SET NOT NULL;

CREATE INDEX idx_books_author ON books (author_id);
CREATE INDEX idx_orders_client ON orders (client_id);
CREATE INDEX idx_orders_issue_date ON orders (issue_date);
CREATE INDEX idx_orders_due_date ON orders (due_date);
CREATE INDEX idx_order_books_book ON order_books (book_id);
CREATE INDEX idx_users_roles_role ON users_roles (role_id);
