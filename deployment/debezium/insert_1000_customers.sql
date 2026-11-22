INSERT INTO customers (name, email, city)
SELECT 
    f.first_name || ' ' || l.last_name,
    LOWER(l.last_name || '.' || f.first_name || ROW_NUMBER() OVER () || '@istad.edu.kh'),
    (ARRAY['Phnom Penh', 'Siem Reap', 'Battambang', 'Kampot', 'Sihanoukville', 'Kandal', 'Kep', 'Koh Kong', 'Takeo', 'Pursat'])[1 + ((ROW_NUMBER() OVER ()) % 10)]
FROM 
    unnest(ARRAY['Chan', 'Keo', 'Sok', 'Sokha', 'Vicheka', 'Heng', 'Lim', 'Tep', 'Mao', 'Nuth', 'Pich', 'Ouk', 'Meng', 'Ros', 'Samnang', 'Bun', 'Chhay', 'Chea', 'Kong', 'Ly', 'Phan', 'Rin', 'San', 'Taing', 'Ung']) AS f(first_name)
CROSS JOIN 
    unnest(ARRAY['Dara', 'Bopha', 'Piseth', 'Rithy', 'Chenda', 'Makara', 'Sovann', 'Socheat', 'Kalyan', 'Sreypov', 'Panha', 'Sothea', 'Khemara', 'Vibol', 'Theary', 'Chantha', 'Vannak', 'Vireak', 'Sophea', 'Sreynich', 'Kosal', 'Monita', 'Rith', 'Nita', 'Thida', 'Seyha', 'Davin', 'Ratanak', 'Saroth', 'Souheng', 'Chhayya', 'Sovannada', 'Solyta', 'Menghour', 'Sothearith', 'Sophearam', 'Rasmey', 'Bona', 'Kanha', 'Chhorvy']) AS l(last_name);
