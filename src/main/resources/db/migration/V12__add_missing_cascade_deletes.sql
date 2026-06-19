-- V12__add_missing_cascade_deletes.sql

-- CRITICAL: Add ON DELETE CASCADE for cascade relationships in Java
-- Store → StoreMember, Product, Customer (all have cascade=CascadeType.ALL)
ALTER TABLE store_members 
    DROP CONSTRAINT store_members_user_id_fkey;
    
ALTER TABLE store_members
    ADD CONSTRAINT fk_store_members_user
        FOREIGN KEY (user_id)
            REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE store_members 
    DROP CONSTRAINT store_members_store_id_fkey;
    
ALTER TABLE store_members
    ADD CONSTRAINT fk_store_members_store
        FOREIGN KEY (store_id)
            REFERENCES stores(id) ON DELETE CASCADE;

-- Customer → Store (cascade)
ALTER TABLE customers
    DROP CONSTRAINT customers_store_id_fkey;
    
ALTER TABLE customers
    ADD CONSTRAINT fk_customers_store
        FOREIGN KEY (store_id)
            REFERENCES stores(id) ON DELETE CASCADE;

-- Customer → User (cascade, nullable)
ALTER TABLE customers
    DROP CONSTRAINT IF EXISTS fk_customers_user;
    
ALTER TABLE customers
    ADD CONSTRAINT fk_customers_user
        FOREIGN KEY (user_id)
            REFERENCES users(id) ON DELETE CASCADE;

-- Product → Store (cascade)
ALTER TABLE products
    DROP CONSTRAINT products_store_id_fkey;
    
ALTER TABLE products
    ADD CONSTRAINT fk_products_store
        FOREIGN KEY (store_id)
            REFERENCES stores(id) ON DELETE CASCADE;

-- ScheduleConfig → Product (cascade)
ALTER TABLE schedule_configs
    DROP CONSTRAINT IF EXISTS schedule_configs_product_id_fkey;
    
ALTER TABLE schedule_configs
    ADD CONSTRAINT fk_schedule_configs_product
        FOREIGN KEY (product_id)
            REFERENCES products(id) ON DELETE CASCADE;

-- Order → Store (cascade)
ALTER TABLE orders
    DROP CONSTRAINT orders_store_id_fkey;
    
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_store
        FOREIGN KEY (store_id)
            REFERENCES stores(id) ON DELETE CASCADE;

-- Order → Customer (cascade)
ALTER TABLE orders
    DROP CONSTRAINT orders_customer_id_fkey;
    
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id) ON DELETE CASCADE;

-- OrderItem → Order (cascade)
ALTER TABLE order_items
    DROP CONSTRAINT order_items_order_id_fkey;
    
ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
            REFERENCES orders(id) ON DELETE CASCADE;

-- OrderItem → Product (cascade)
ALTER TABLE order_items
    DROP CONSTRAINT order_items_product_id_fkey;
    
ALTER TABLE order_items
    ADD CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id)
            REFERENCES products(id) ON DELETE CASCADE;

-- Appointment → OrderItem (cascade, OneToOne)
ALTER TABLE appointments
    DROP CONSTRAINT IF EXISTS appointments_order_item_id_fkey;
    
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_order_item
        FOREIGN KEY (order_item_id)
            REFERENCES order_items(id) ON DELETE CASCADE;

-- Appointment → Customer (cascade)
ALTER TABLE appointments
    DROP CONSTRAINT appointments_customer_id_fkey;
    
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_customer
        FOREIGN KEY (customer_id)
            REFERENCES customers(id) ON DELETE CASCADE;

-- Appointment → Resource (cascade)
ALTER TABLE appointments
    DROP CONSTRAINT IF EXISTS fk_appointments_resource;
    
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_resource
        FOREIGN KEY (resource_id)
            REFERENCES resources(id) ON DELETE CASCADE;

-- Resource → Store (cascade)
ALTER TABLE resources
    DROP CONSTRAINT resources_store_id_fkey;
    
ALTER TABLE resources
    ADD CONSTRAINT fk_resources_store
        FOREIGN KEY (store_id)
            REFERENCES stores(id) ON DELETE CASCADE;

-- Resource → StoreMember (cascade, nullable)
ALTER TABLE resources
    DROP CONSTRAINT IF EXISTS resources_store_member_id_fkey;
    
ALTER TABLE resources
    ADD CONSTRAINT fk_resources_store_member
        FOREIGN KEY (store_member_id)
            REFERENCES store_members(id) ON DELETE SET NULL;

-- ResourceAvailability → Resource (cascade)
ALTER TABLE resource_availabilities
    DROP CONSTRAINT IF EXISTS resource_availabilities_resource_id_fkey;
    
ALTER TABLE resource_availabilities
    ADD CONSTRAINT fk_resource_availabilities_resource
        FOREIGN KEY (resource_id)
            REFERENCES resources(id) ON DELETE CASCADE;

-- ResourceAvailabilityException → Resource (cascade)
ALTER TABLE resource_availability_exceptions
    DROP CONSTRAINT IF EXISTS resource_availability_exceptions_resource_id_fkey;
    
ALTER TABLE resource_availability_exceptions
    ADD CONSTRAINT fk_resource_availability_exceptions_resource
        FOREIGN KEY (resource_id)
            REFERENCES resources(id) ON DELETE CASCADE;

-- RegisteredClient → Store (cascade)
ALTER TABLE registered_clients
    DROP CONSTRAINT IF EXISTS registered_clients_store_id_fkey;
    
ALTER TABLE registered_clients
    ADD CONSTRAINT fk_registered_clients_store
        FOREIGN KEY (store_id)
            REFERENCES stores(id) ON DELETE CASCADE;

-- AuditLog → User (no cascade, references only)
ALTER TABLE audit_logs
    DROP CONSTRAINT IF EXISTS audit_logs_user_id_fkey;
    
ALTER TABLE audit_logs
    ADD CONSTRAINT fk_audit_logs_user
        FOREIGN KEY (user_id)
            REFERENCES users(id) ON DELETE SET NULL;
