CREATE DATABASE hotel_booking;

CREATE TABLE contact_info (
    id BIGINT PRIMARY KEY,
    complete_address VARCHAR(255),
    location VARCHAR(255),
    email VARCHAR(255),
    phone_number VARCHAR(50)
);


CREATE TABLE hotel (
    id BIGINT PRIMARY KEY,
    city VARCHAR(255),
    contact_info_id BIGINT UNIQUE,
    photos TEXT[],
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    amenities TEXT[],
    active BOOLEAN,
    CONSTRAINT fk_hotel_contact_info
        FOREIGN KEY (contact_info_id) REFERENCES contact_info(id)
);


CREATE TABLE room (
    id BIGINT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    type VARCHAR(100),
    base_price NUMERIC(12,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    amenities TEXT[],
    photos TEXT[],
    total_count INTEGER,
    capacity INTEGER,
    CONSTRAINT fk_room_hotel
        FOREIGN KEY (hotel_id) REFERENCES hotel(id)
);


CREATE TABLE inventory (
    id BIGINT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    date DATE,
    booked_count INTEGER,
    total_count INTEGER,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    surge_factor NUMERIC(10,2),
    closed BOOLEAN,
    CONSTRAINT fk_inventory_hotel
        FOREIGN KEY (hotel_id) REFERENCES hotel(id),
    CONSTRAINT fk_inventory_room
        FOREIGN KEY (room_id) REFERENCES room(id)
);


CREATE UNIQUE INDEX uq_inventory_room_date
    ON inventory (room_id, date);


CREATE TABLE "user" (
    id BIGINT PRIMARY KEY,
    roles TEXT[],
    name VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255)
);

CREATE UNIQUE INDEX uq_user_email
    ON "user" (email);


CREATE TABLE guest (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255),
    created_at TIMESTAMP,
    gender VARCHAR(50),
    CONSTRAINT fk_guest_user
        FOREIGN KEY (user_id) REFERENCES "user"(id)
);


CREATE TABLE payment (
    id BIGINT PRIMARY KEY,
    transaction_id VARCHAR(255),
    price NUMERIC(12,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50)
);

CREATE UNIQUE INDEX uq_payment_transaction_id
    ON payment (transaction_id);


CREATE TABLE booking (
    id BIGINT PRIMARY KEY,
    hotel_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    status VARCHAR(50),
    checkin_date DATE,
    checkout_date DATE,
    payment_id BIGINT UNIQUE,
    CONSTRAINT fk_booking_hotel
        FOREIGN KEY (hotel_id) REFERENCES hotel(id),
    CONSTRAINT fk_booking_room
        FOREIGN KEY (room_id) REFERENCES room(id),
    CONSTRAINT fk_booking_user
        FOREIGN KEY (user_id) REFERENCES "user"(id),
    CONSTRAINT fk_booking_payment
        FOREIGN KEY (payment_id) REFERENCES payment(id),
    CONSTRAINT chk_booking_dates
        CHECK (checkout_date IS NULL OR checkin_date IS NULL OR checkout_date >= checkin_date)
);


CREATE TABLE booking_guest (
    id BIGINT PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    guest_id BIGINT NOT NULL,
    CONSTRAINT fk_booking_guest_booking
        FOREIGN KEY (booking_id) REFERENCES booking(id),
    CONSTRAINT fk_booking_guest_guest
        FOREIGN KEY (guest_id) REFERENCES guest(id),
    CONSTRAINT uq_booking_guest UNIQUE (booking_id, guest_id)
);


CREATE INDEX idx_room_hotel_id ON room(hotel_id);
CREATE INDEX idx_inventory_hotel_id ON inventory(hotel_id);
CREATE INDEX idx_inventory_room_id ON inventory(room_id);
CREATE INDEX idx_inventory_date ON inventory(date);
CREATE INDEX idx_guest_user_id ON guest(user_id);
CREATE INDEX idx_booking_hotel_id ON booking(hotel_id);
CREATE INDEX idx_booking_room_id ON booking(room_id);
CREATE INDEX idx_booking_user_id ON booking(user_id);
CREATE INDEX idx_booking_status ON booking(status);
CREATE INDEX idx_payment_status ON payment(status);

SHOW TABLES;