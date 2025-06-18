CREATE TABLE rentals (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         user_id BIGINT NOT NULL,
                         book_id BIGINT NOT NULL,
                         rented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         due_date DATE NOT NULL,
                         returned_at TIMESTAMP,

                         CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
);
