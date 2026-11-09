--liquibase formatted sql
--changeset iam:006-seed-system-roles-and-permissions

INSERT INTO roles (uuid, name, description, is_system, is_active)
VALUES
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404200', 'ADMIN', 'System administrator', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404201', 'USER', 'Standard user', TRUE, TRUE)
ON CONFLICT (name) DO NOTHING;

INSERT INTO permissions (uuid, name, resource, action, description, is_system, is_active)
VALUES
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404300', 'user:create', 'user', 'create', 'Create users', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404301', 'user:read', 'user', 'read', 'Read users', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404302', 'user:update', 'user', 'update', 'Update users', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404303', 'user:delete', 'user', 'delete', 'Delete users', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404304', 'user:assign', 'user', 'assign', 'Assign user authorities', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404305', 'role:create', 'role', 'create', 'Create roles', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404306', 'role:read', 'role', 'read', 'Read roles', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404307', 'role:update', 'role', 'update', 'Update roles', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404308', 'role:delete', 'role', 'delete', 'Delete roles', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404309', 'role:assign', 'role', 'assign', 'Assign permissions to roles', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404310', 'permission:create', 'permission', 'create', 'Create permissions', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404311', 'permission:read', 'permission', 'read', 'Read permissions', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404312', 'permission:update', 'permission', 'update', 'Update permissions', TRUE, TRUE),
    ('ce5a2c2d-3e75-4c10-ae4c-c51600404313', 'permission:delete', 'permission', 'delete', 'Delete permissions', TRUE, TRUE)
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_permissions (role_id, permission_id, assigned_by, reason)
SELECT roles.id, permissions.id, 'SYSTEM', 'Initial system administrator permissions'
FROM roles CROSS JOIN permissions
WHERE roles.name = 'ADMIN'
ON CONFLICT (role_id, permission_id) DO NOTHING;
