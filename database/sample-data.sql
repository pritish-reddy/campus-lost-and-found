-- Optional demo data. Run manually:
--   psql -U postgres -d lost_found_db -f database/sample-data.sql
INSERT INTO lost_items (item_name, description, location) VALUES
 ('Black Wallet', 'Leather wallet with an ID card inside', 'Library'),
 ('Blue Water Bottle', 'Steel bottle with a sticker on the lid', 'Main Canteen');

INSERT INTO found_items (item_name, description, location) VALUES
 ('Hostel Room Keys', 'Three keys on a red keychain', 'Block A Corridor'),
 ('Scientific Calculator', 'Casio calculator, name scratched on the back', 'Lecture Hall 3'),
 ('Black Umbrella', 'Folding umbrella with a wooden handle', 'Bus Stop');