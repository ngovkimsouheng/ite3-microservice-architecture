import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import AppLayout from './layouts/AppLayout';
import ProfilePage from './pages/account/ProfilePage';
import SecurityPage from './pages/account/SecurityPage';
import LinkedAccountsPage from './pages/account/LinkedAccountsPage';
import SessionsPage from './pages/account/SessionsPage';
import HistoryPage from './pages/account/HistoryPage';
import LoginPage from './pages/auth/LoginPage';

// Admin Module Pages (+50 pts)
import OverviewPage from './pages/admin/OverviewPage';
import UsersPage from './pages/admin/UsersPage';
import UserDetailPage from './pages/admin/UserDetailPage';
import RolesPage from './pages/admin/RolesPage';
import ClientsPage from './pages/admin/ClientsPage';
import AuditPage from './pages/admin/AuditPage';
import SystemConfigPage from './pages/admin/SystemConfigPage';

export default function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Public Login Route */}
                <Route path="/login" element={<LoginPage />} />

                {/* Default Redirect */}
                <Route path="/" element={<Navigate to="/account/profile" replace />} />

                {/* Protected Dashboard Layout: Account Module (+40 pts) & Management Module (+50 pts) */}
                <Route element={<AppLayout />}>
                    {/* Account Settings */}
                    <Route path="/account/profile" element={<ProfilePage />} />
                    <Route path="/account/security" element={<SecurityPage />} />
                    <Route path="/account/linked-accounts" element={<LinkedAccountsPage />} />
                    <Route path="/account/sessions" element={<SessionsPage />} />
                    <Route path="/account/history" element={<HistoryPage />} />

                    {/* Management Module */}
                    <Route path="/admin/overview" element={<OverviewPage />} />
                    <Route path="/admin/users" element={<UsersPage />} />
                    <Route path="/admin/users/:uuid" element={<UserDetailPage />} />
                    <Route path="/admin/roles" element={<RolesPage />} />
                    <Route path="/admin/clients" element={<ClientsPage />} />
                    <Route path="/admin/audit" element={<AuditPage />} />
                    <Route path="/admin/system" element={<SystemConfigPage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
