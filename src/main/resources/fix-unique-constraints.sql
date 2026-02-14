-- Fix cafe-wise uniqueness. Run this ONCE if you get "Duplicate entry" when adding tables.
-- Old schema had table_number unique across entire DB; we need (cafe_id, table_number) per cafe.

-- Drop old constraint on table_number alone (exact name from your error)
ALTER TABLE restaurant_tables DROP INDEX UK1t9vy8tg8eidmfw4k887ibeu1;

-- Add cafe-wise unique (safe to run; will fail harmlessly if already exists)
ALTER TABLE restaurant_tables ADD UNIQUE KEY uk_table_cafe_number (cafe_id, table_number);
